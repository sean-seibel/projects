use std::cmp::{max, max_by, min, min_by};
use crate::{Black, Board, GameOver, Move, White};
use crate::board::{on_board, Square, square_to_string};
use crate::piece::{Color, Piece, PieceType};
use crate::piece::Piece::*;
use crate::piece::PieceType::*;

impl Board {
    pub fn suggest_move(&self) -> Move {
        // let moves = self.slow_enumerate_moves();
        // *moves.choose(&mut rand::thread_rng()).expect("Game over!")
        let depth = 20u16;
        self.choose_by_minmax(depth)
    }

    fn choose_by_minmax(&self, depth: u16) -> Move {
        let color = self.active_color;
        let opponent = self.active_color.switch();
        let guaranteed_win = if color==White { WHITE_WIN } else { BLACK_WIN }; // find max move if white
        let guaranteed_loss = if color==White { BLACK_WIN } else { WHITE_WIN };
        let gl: fn(i16, i16) -> bool = if color==White { |prev, cur| cur >= prev } else { |prev, cur| cur <= prev };

        let mv = self.slow_enumerate_moves(color);

        let mut best_move_eval = guaranteed_loss;
        let mut best_move = Move { from: Square { rank: 9, file: 9 }, to: Square { rank: 9, file: 9 }};

        for mov in mv {
            let eval = self.freeze().minmax_eval(mov, color, depth);
            if gl(best_move_eval, eval) {
                best_move_eval = eval;
                best_move = mov;
            }
        }

        best_move
    }

    //color = color of testing_move
    fn minmax_eval(&mut self, testing_move: Move, color: Color, depth: u16) -> i16 {
        self.make_move(testing_move);
        let opponent = color.switch();
        let non_captures = self.slow_enumerate_noncaptures(opponent);
        let captures = self.slow_enumerate_captures(opponent);

        if non_captures.is_empty() && captures.is_empty() {
            return if Self::in_check(&self.state, White) {
                BLACK_WIN
            } else if Self::in_check(&self.state, Black) {
                WHITE_WIN
            } else {
                0
            };
        }

        if depth < 10 {
            return self.immediate_analysis();
        }

        // If White is moving, check for best response from Black
        let min_or_max: fn(i16, i16) -> i16 = if color==White { min } else { max };

        let mut best_response_eval = if color==White { WHITE_WIN } else { BLACK_WIN };

        let guaranteed_loss = if color==White { BLACK_WIN } else { WHITE_WIN };

        // let mut move_total = 0;
        for mov in captures {
            let eval = self.freeze().minmax_eval(
                mov,
                opponent,
                depth - 3,
            );
            // move_total += 1;
            // println!("{}:{} :{} | {}-{} by {:?}" , depth, move_total, eval, square_to_string(mov.from), square_to_string(mov.to), opponent);
            best_response_eval = min_or_max(
                best_response_eval,
                eval
            );
            if best_response_eval == guaranteed_loss { break; }
        }

        for mov in non_captures {
            let eval = self.freeze().minmax_eval(
                mov,
                opponent,
                depth - 10,
            );
            // move_total += 1;
            // println!("{}:{} :{} | {}-{} by {:?}" , depth, move_total, eval, square_to_string(mov.from), square_to_string(mov.to), opponent);
            best_response_eval = min_or_max(
                best_response_eval,
                eval
            );
            if best_response_eval == guaranteed_loss { break; }
        }

        // if depth == 2 {
        //     println!("^ testing:{}-{} by {:?}, found:{}", square_to_string(testing_move.from), square_to_string(testing_move.to), color, best_response_eval);
        // }
        best_response_eval
    }

    pub fn influence_grid(board: &[[Piece; 8]; 8]) -> [[i8; 8]; 8]{
        let mut infs = [[0i8; 8]; 8];

        for rank in 0u8..8 {
            for file in 0u8..8 {
                let piece = board[rank as usize][file as usize];
                let sq = &Square { rank, file };
                match piece {
                    Piece::Empty => {},
                    WKing => Self::populate_sing_offsets(&mut infs, 1, [[1,1],[0,1],[-1,1],[-1,0],[-1,-1],[0,-1],[1,-1],[1,0]].iter(), sq),
                    WQueen => { Self::populate_orth_offsets(&mut infs, board, 1, sq, White);
                        Self::populate_diag_offsets(&mut infs, board, 1, sq, White);
                    },
                    WRook => Self::populate_orth_offsets(&mut infs, board, 1, sq, White),
                    WBishop => Self::populate_diag_offsets(&mut infs, board, 1, sq, White),
                    WKnight => Self::populate_sing_offsets(&mut infs, 1, [[2,1],[1,2],[-2,1],[1,-2],[2,-1],[-1,2],[-2,-1],[-1,-2]].iter(), sq),
                    WPawn => Self::populate_sing_offsets(&mut infs, 1, [[1, 1], [1, -1]].iter(), sq),
                    BKing => Self::populate_sing_offsets(&mut infs, -1, [[1,1],[0,1],[-1,1],[-1,0],[-1,-1],[0,-1],[1,-1],[1,0]].iter(), sq),
                    BQueen => { Self::populate_orth_offsets(&mut infs, board, -1, sq, Black);
                        Self::populate_diag_offsets(&mut infs, board, -1, sq, Black);
                    },
                    BRook => Self::populate_orth_offsets(&mut infs, board, -1, sq, Black),
                    BBishop => Self::populate_diag_offsets(&mut infs, board, -1, sq, Black),
                    BKnight => Self::populate_sing_offsets(&mut infs, -1, [[2,1],[1,2],[-2,1],[1,-2],[2,-1],[-1,2],[-2,-1],[-1,-2]].iter(), sq),
                    BPawn => Self::populate_sing_offsets(&mut infs, -1, [[-1, 1], [-1, -1]].iter(), sq),
                }
            }
        }

        infs
    }

    // add x-raying
    fn populate_orth_offsets(infs: &mut [[i8;8];8], board: &[[Piece; 8]; 8], diff: i8, sq: &Square, color: Color) {
        let cont_if = [Piece::Empty, Rook.of_color(color), Queen.of_color(color)];
        for offset in [[1,0],[0,1],[-1,0],[0,-1]] {
            let mut sq = [sq.rank as i8, sq.file as i8];
            sq[0] += offset[0]; sq[1] += offset[1];
            if !on_board(sq) { continue; }
            while on_board(sq) {
                infs[sq[0]as usize][sq[1]as usize] += diff;
                if !cont_if.contains(&board[sq[0]as usize][sq[1]as usize]) { break; }
                sq[0] += offset[0]; sq[1] += offset[1];
            }
        }
    }

    fn populate_diag_offsets(infs: &mut [[i8;8];8], board: &[[Piece; 8]; 8], diff: i8, sq: &Square, color: Color) {
        let cont_if = [Piece::Empty, Bishop.of_color(color), Queen.of_color(color)];
        let pawn_continuations = if color==White { [[1i8, 1], [1, -1]] } else { [[-1, 1], [-1, -1]] };
        for offset in [[1,1],[-1,1],[1,-1],[-1,-1]] {
            let mut sq = [sq.rank as i8, sq.file as i8];
            sq[0] += offset[0]; sq[1] += offset[1];
            if !on_board(sq) { continue; }
            while on_board(sq) {
                infs[sq[0]as usize][sq[1]as usize] += diff;
                if !cont_if.contains(&board[sq[0]as usize][sq[1]as usize]) {
                    if on_board([sq[0]+offset[0],sq[1]+offset[1]]) && board[sq[0]as usize][sq[1]as usize] == Pawn.of_color(color) && pawn_continuations.contains(&offset)  {
                        infs[(sq[0]+offset[0])as usize][(sq[1]+offset[1])as usize] += diff
                    }
                    break;
                }
                sq[0] += offset[0]; sq[1] += offset[1];
            }
        }
    }

    fn populate_sing_offsets<'a, I>(infs: &mut [[i8;8];8], diff: i8, offsets: I, sq: &Square)
        where I: Iterator<Item=&'a[i8; 2]>
    {
        for offset in offsets {
            let sq = [sq.rank as i8 + offset[0] , sq.file as i8 + offset[1]];
            if !on_board(sq) { continue; }
            infs[sq[0]as usize][sq[1]as usize] += diff;
        }
    }
}

impl Color {
    fn inf(&self) -> i8 {
        match self {
            Color::None => 0,
            White => 1,
            Black => -1,
        }
    }
}

const WHITE_WIN: i16 = i16::MAX;
const BLACK_WIN: i16 = i16::MIN;

impl Board {
    pub fn immediate_analysis(&self) -> i16 {
        return Self::material_count(&self.state) * 100 + Self::influence_coeff(&self.state, Self::influence_grid(&self.state))
    }

    pub fn influence_coeff(board: &[[Piece;8];8], infs: [[i8;8];8]) -> i16 {
        let mut sum = 0i16;
        for r in 0..8 {
            for f in 0..8 {
                sum += infs[r][f] as i16;
                match board[r][f].color() {
                    Color::None => {}
                    White => sum += (infs[r][f] * 2 - infs[r][f].abs()) as i16, // if infs[r][f] is negative, x3 else x1
                    Black => sum += (infs[r][f] * 2 + infs[r][f].abs()) as i16
                }
                if r >= 3 && r <= 4 && f >= 3 && f <= 4 {
                    sum += infs[r][f] as i16;
                }
            }
        }
        sum
    }

    pub fn material_count(board: &[[Piece; 8]; 8]) -> i16 {
        let mut white_total = 0i16;
        let mut black_total = 0i16;

        for rank in 0..8 {
            for file in 0..8 {
                let piece = board[rank][file];
                let material_eval = piece.material_eval(rank as u8, file as u8);
                match piece.color() {
                    Color::None => {}
                    White => white_total += material_eval,
                    Black => black_total += material_eval,
                }
            }
        }

        white_total - black_total
    }
}

impl Piece {
    //always pos
    fn material_eval(&self, rank: u8, file: u8) -> i16 {
        match self {
            Piece::Empty => 0,
            WKing => 60,
            WQueen => 10,
            WRook => 5,
            WBishop => 3,
            WKnight => 3,
            WPawn => match rank {
                6 => 3,
                _ => 1,
            }
            BKing => 60,
            BQueen => 10,
            BRook => 5,
            BBishop => 3,
            BKnight => 3,
            BPawn => match rank {
                1 => 3,
                _ => 1,
            }
        }
    }
}

