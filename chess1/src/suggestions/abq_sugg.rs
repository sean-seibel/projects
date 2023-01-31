use std::cmp::{max, min};
use std::process::exit;
use std::time::Instant;
use crate::{Black, Board, Move, smart_influence_grid_double, tension_against_color, tensions, White};
use crate::board::InitialState;
use crate::piece::Color;

// next idea: don't copy the board, except for the first time to freeze it
// after that, add and remove moves
// minmax_xyz should act on
impl Board {
    pub fn abq_suggest_move(&mut self) -> Move {
        self.abq_suggest_move_depth(3)
    }

    pub fn abq_suggest_move_depth(&mut self, depth: u16) -> Move {
        let color = self.active_color;

        let possibilities = self.slow_enumerate_hopeful_order(color);
        let mut test_board = self.freeze();
        let init = &self.freeze_init();

        let first_step = if color==White { Self::minmax_min_q } else { Self::minmax_max_q };
        let mut best_move = *possibilities.first().unwrap();

        let mut alpha = i32::MIN;
        let mut beta = i32::MAX;

        // I feel like sometimes this causes excessive pruning
        // and this results in taking the first found move (rook on a1 or black a pawn)
        //  let start_time = Instant::now();
        /* if self.can_null_move() && false {
            let mut dummy_board = self.freeze();
            let dummy_init = &self.freeze_init();
            dummy_board.active_color = dummy_board.active_color.switch();
            let color = dummy_board.active_color;
            let min_or_max = if color==White{max}else{min};
            let mut best_eval = if color==White{i32::MIN}else{i32::MAX};

            let nulls = dummy_board.slow_enumerate_moves(color);

            for null in nulls {
                dummy_board.make_move(null);
                best_eval = min_or_max(
                    best_eval, dummy_board.analysis_with_imminent_attacks()
                );
                dummy_board.undo(dummy_init);
            }

            // println!("{}", dummy_board.as_string());
            //  println!("time elapsed in null-move search:{:?}", start_time.elapsed());
            //  exit(0);

            if color==White { // black making original move
                beta = best_eval; // remember we might get white having M1
            } else { // white making original move
                alpha = best_eval;
            }
        } */

        for mov in possibilities {
            test_board.make_move(mov);
            let eval = first_step(&mut test_board, depth, alpha, beta, color, init);
            test_board.undo(init);
            if color == White {
                if alpha < eval {
                    best_move = mov;
                    alpha = eval;
                }
            } else {
                if beta > eval {
                    best_move = mov;
                    beta = eval;
                }
            }
        }

        best_move
    }

    //black to move
    fn minmax_min_q(&mut self, depth: u16, alpha: i32, beta: i32, original_move_color: Color, init: &InitialState) -> i32 {
        let moves = self.slow_enumerate_hopeful_order(Black);
        // let cats = self.slow_enumerate_categories(Black);
        // let checks = cats.0;
        // let captures = cats.1;

        if alpha >= beta {
            // if depth >= 2 { println!("pruning at depth {}", depth); }
            return WHITE_WIN // don't investigate this branch, white might as well win
        }
        if moves.is_empty() {
            return if Self::in_check(&self.state, White) {
                BLACK_WIN // I don't think this is possible
            } else if Self::in_check(&self.state, Black) {
                WHITE_WIN
            } else {
                0
            };
        } // check if node is terminal

        if depth == 0 {
            return self.immediate_analysis();
        }

        // let (winfs, binfs) = smart_influence_grid_double(&self.state);
        // let t = tensions(&self.state, &winfs, &binfs);
        // let tens = tension_against_color(&t, original_move_color);

        // if tens == 0 {
        //     return self.immediate_analysis();
        // }

        let mut beta = beta; // only update beta on this row

        for mov in moves {
            self.make_move(mov);
            let eval = self.minmax_max_q(
                depth - 1,
                alpha,
                beta,
                original_move_color,
                init
            );
            self.undo(init);
            beta = min(eval, beta)
        }

        max(alpha, beta)
    }

    //white to move
    fn minmax_max_q(&mut self, depth: u16, alpha: i32, beta: i32, original_move_color: Color, init: &InitialState) -> i32 {
        let moves = self.slow_enumerate_hopeful_order(White);

        if alpha >= beta {
            // if depth >= 2 { println!("pruning at depth {}", depth); }
            return BLACK_WIN // don't investigate this branch, black might as well win
        }
        if moves.is_empty() {
            return if Self::in_check(&self.state, White) {
                BLACK_WIN // I don't think this is possible
            } else if Self::in_check(&self.state, Black) {
                WHITE_WIN
            } else {
                0
            };
        } // check if node is terminal

        if depth == 0 {
            return self.immediate_analysis();
        }

        // let (winfs, binfs) = smart_influence_grid_double(&self.state);
        // let t = tensions(&self.state, &winfs, &binfs);
        // let tens = tension_against_color(&t, original_move_color);

        // if tens == 0 {
        //     return self.immediate_analysis();
        // }

        let mut alpha = alpha; // only update beta on this row

        for mov in moves {
            self.make_move(mov);
            let eval = self.minmax_min_q(
                depth - 1,
                alpha,
                beta,
                original_move_color,
                init
            );
            self.undo(init);
            alpha = max(eval, alpha);
        }

        min(alpha, beta)
    }

    fn can_null_move(&self) -> bool {
        !Self::in_check(&self.state, self.active_color)
    }
}

const WHITE_WIN: i32 = i32::MAX;
const BLACK_WIN: i32 = i32::MIN;