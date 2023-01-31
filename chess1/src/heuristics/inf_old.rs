use crate::board::{on_board, Square};
use crate::piece::{Color, Piece};
use crate::piece::Color::{White, Black};
use crate::piece::Piece::*;
use crate::piece::PieceType::{Bishop, Pawn, Queen, Rook};

pub fn influence_coeff(board: &[[Piece;8];8], infs: &[[i8;8];8]) -> i32 {
    let mut sum = 0i32;
    for r in 0..8 {
        for f in 0..8 {
            let mut addition = infs[r][f] as i32;
            match board[r][f].color() {
                Color::None => continue,
                White => addition += (infs[r][f] * 2 - infs[r][f].abs()) as i32, // if infs[r][f] is negative, x3 else x1
                Black => addition += (infs[r][f] * 2 + infs[r][f].abs()) as i32
            }
            match board[r][f].color() {
                Color::None => {}
                White => addition *= W_INF_TARGETS[r][f],
                Black => addition *= W_INF_TARGETS[7-r][f]
            }

            sum += addition;
        }
    }
    sum
}

const W_INF_TARGETS: [[i32;8];8] = [
    [1,1,1,1,1,1,1,1],
    [2,3,3,4,4,3,3,2],
    [1,2,2,3,3,2,2,1],
    [1,1,4,6,6,4,1,1],
    [1,1,1,3,3,1,1,1],
    [1,1,1,1,1,1,1,1],
    [2,2,2,1,1,2,2,2],
    [1,1,1,1,1,1,1,1],
];

// scale of 1-10
pub fn influence_grid(board: &[[Piece; 8]; 8]) -> [[i8; 8]; 8]{
    let mut infs = [[0i8; 8]; 8];

    for rank in 0u8..8 {
        for file in 0u8..8 {
            let piece = board[rank as usize][file as usize];
            let sq = &Square { rank, file };
            match piece {
                Piece::Empty => {},
                WKing => populate_sing_offsets(&mut infs, 1, [[1,1],[0,1],[-1,1],[-1,0],[-1,-1],[0,-1],[1,-1],[1,0]].iter(), sq),
                WQueen => { populate_orth_offsets(&mut infs, board, 2, sq, White);
                    populate_diag_offsets(&mut infs, board, 2, sq, White);
                },
                WRook => populate_orth_offsets(&mut infs, board, 4, sq, White),
                WBishop => populate_diag_offsets(&mut infs, board, 5, sq, White),
                WKnight => populate_sing_offsets(&mut infs, 6, [[2,1],[1,2],[-2,1],[1,-2],[2,-1],[-1,2],[-2,-1],[-1,-2]].iter(), sq),
                WPawn => populate_sing_offsets(&mut infs, 10, [[1, 1], [1, -1]].iter(), sq),
                BKing => populate_sing_offsets(&mut infs, -1, [[1,1],[0,1],[-1,1],[-1,0],[-1,-1],[0,-1],[1,-1],[1,0]].iter(), sq),
                BQueen => { populate_orth_offsets(&mut infs, board, -2, sq, Black);
                    populate_diag_offsets(&mut infs, board, -2, sq, Black);
                },
                BRook => populate_orth_offsets(&mut infs, board, -4, sq, Black),
                BBishop => populate_diag_offsets(&mut infs, board, -5, sq, Black),
                BKnight => populate_sing_offsets(&mut infs, -6, [[2,1],[1,2],[-2,1],[1,-2],[2,-1],[-1,2],[-2,-1],[-1,-2]].iter(), sq),
                BPawn => populate_sing_offsets(&mut infs, -10, [[-1, 1], [-1, -1]].iter(), sq),
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