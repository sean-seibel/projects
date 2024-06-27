from ros2topic.api import get_topic_names
from ros2topic.api import get_msg_class
import rclpy
from rclpy.node import Node

from dataclasses import dataclass, field

from time import time

from typing import List

import threading

import curses

NO_TIME = 0.0
MAX_WINDOW = 5


@dataclass
class topic_info:
    sub: any
    line: int
    last_time: float = NO_TIME
    last_times: List[float] = field(default_factory=list)
    update: int = 0

    def received_tick(self, time):
        if self.last_time == NO_TIME:
            self.last_time = time
            return
        elapsed = (time - self.last_time)
        self.last_time = time
        if len(self.last_times) < MAX_WINDOW:
            self.last_times.append(elapsed)
        else:
            self.last_times[self.update] = elapsed
        self.update = (self.update + 1) % MAX_WINDOW
    
    def reading(self) -> float:
        return sum(self.last_times) / len(self.last_times) if self.last_times else NO_TIME

class MonitorNode(Node):
    def __init__(self, stdscr: curses.window) -> None:
        super().__init__("ros_activity_monitor")
        self.term = stdscr
        self.lines = 0
        self.termheight , _ = self.term.getmaxyx()
        self.topline = 0
        self.leftcol = 0
        self.max_length = 0
        self.listeners = {}
        self.write_mutex = threading._RLock()
        self.update_list()
        key_check = threading.Thread(target=self._key_check, daemon=True)
        key_check.start()
        self.create_timer(2.0, self.update_list)
        self.create_timer(0.25, self.initial_print)

    def _key_check(self):
        self.term.nodelay(True)
        while True:
            ch = self.term.getch()
            if ch == curses.KEY_RESIZE:
                self.termheight , _ = self.term.getmaxyx()
                self.initial_print()
            elif ch == curses.KEY_UP and self.topline > 0:
                self.topline -= 1
                self.initial_print()
            elif ch == curses.KEY_DOWN and self.lines - self.topline > self.termheight:
                self.topline += 1
                self.initial_print()
            elif ch == curses.KEY_RIGHT and self.leftcol > 0:
                self.leftcol -= 1
                self.initial_print()
            elif ch == curses.KEY_LEFT and self.leftcol < self.max_length:
                self.leftcol += 1
                self.initial_print()

    def write_info(self, info: topic_info):
        termheight , termwidth = self.term.getmaxyx()
        wlength = termwidth - (self.max_length + 1 - self.leftcol)
        line = info.line - self.topline
        reading = info.reading()
        time_since_last = time() - info.last_time
        if wlength > 0 and line >= 0 and line < termheight and info.last_time != NO_TIME:
            reading_str = f"{reading:8.4f}" if reading != NO_TIME else "--------"
            st = f"{info.line:3} {reading_str} |{time_since_last:8.4f}"
            writecol = self.max_length + 1 - self.leftcol
            if writecol < 3:
                st = st[3-writecol:]
                writecol = 3
            self.write_mutex.acquire()
            self.term.addnstr(line, writecol, st, wlength)
            self.write_mutex.release()

    def _create_callback(self, info: topic_info):
        def cb(msg):
            info.received_tick(time())
        return cb
    
    def initial_print(self):
        self.term.erase()
        termheight , termwidth = self.term.getmaxyx()
        self.write_mutex.acquire()
        self.term.addnstr(0,0,f"topic ({len(self.listeners)}) | avg time between 2 received msgs | time since last msg", termwidth)
        self.write_mutex.release()
        for t, inf in self.listeners.items():
            pline = inf.line - self.topline
            if pline >= 1 and pline < termheight:
                st = f"{t}"[self.leftcol:]
                self.write_mutex.acquire()
                self.term.addstr(pline, 0, f"{inf.line}")
                self.term.addnstr(pline, 3, st, termwidth - 3)
                self.write_mutex.release()
                self.write_info(inf)

    def update_list(self):
        topics = get_topic_names(node=self)
        for t in topics:
            if t not in self.listeners and not t.endswith("/state"): # apparently this is deprecated ???
                self.lines += 1
                self.max_length = max(self.max_length, len(t) + 4)
                mtype = get_msg_class(node=self, topic=t)
                self.listeners[t] = topic_info(
                    sub=None,
                    line=self.lines
                )
                self.listeners[t].sub = self.create_subscription(topic=t, msg_type=mtype, callback=self._create_callback(self.listeners[t]), qos_profile=10)

def main(stdscr):
    rclpy.init()
    node = MonitorNode(stdscr)
    rclpy.spin(node)

if __name__=="__main__":
    try:
        curses.wrapper(main)
    except: # idk error msgs are annoying
        pass