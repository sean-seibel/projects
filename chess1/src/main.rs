use std::io::stdin;
use crate::board::{Board, Move, parse_move, print_all_moves};
use crate::piece::Color::{Black, White};
use termion::{clear,cursor};
use crate::piece::{BKINGSIDE, BQUEENSIDE, GameOver, WKINGSIDE, WQUEENSIDE};

mod piece;
mod board;
mod suggest;

fn main() {
    let mut board = Board::default();
    let mut last_input = String::from("");
    let mut active_color = White;
    print!("{}", clear::All);
    print!("{}", cursor::Goto(1,1));
    loop {
        println!("{}", board.as_string());
        let res = board.game_over();
        match res {
            GameOver::NotOver => {}
            GameOver::Checkmate(_) => { println!("Checkmate"); break; }
            GameOver::Draw => { println!("Stalemate"); break; }
        }
        last_input.clear();
        stdin().read_line(&mut last_input).expect("Failed to read from stdin");
        print!("{}", clear::All);
        print!("{}", cursor::Goto(1,1));
        let input = last_input.trim_end();
        if input == "q" { break; }
        if input == "enum" {
            print_all_moves(board.slow_enumerate_moves(
                if board.history.len()%2==0 { White } else { Black }
            )); //vec![ Move{from:Square{rank:1,file:0},to:Square{rank:3,file:0}} ]
            continue;
        }
        if input == "hist" {
            print_all_moves(board.history.clone());
            continue;
        }
        if input == "mat" {
            println!("{}", Board::material_count(&board.state));
            continue;
        }
        if input == "inf" {
            let infs = Board::influence_grid(&board.state);
            println!("{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}\n{:?}",
                     infs[7], infs[6], infs[5], infs[4], infs[3], infs[2], infs[1], infs[0]);
            continue;
        }
        if input == "cast" {
            println!("{}", board.castle_rights);
            let wks = board.castle_rights & WKINGSIDE > 0;
            let wqs = board.castle_rights & WQUEENSIDE > 0;
            let bks = board.castle_rights & BKINGSIDE > 0;
            let bqs = board.castle_rights & BQUEENSIDE > 0;
            println!("bks:{}, bqs:{}, wks:{}, wqs:{}", bks, bqs, wks, wqs);
            continue;
        }
        let mov = if input == "ai" { board.suggest_move() } else {
            parse_move(&last_input)
        };
        if board.check_move(mov, active_color) {
            board.make_move(mov);
            active_color = if active_color == White { Black } else { White };
        } else {
            println!("Invalid move")
        }
    }
}
