use std::cmp::min;
use crate::{Black, White};
use crate::board::{on_board, Square};
use crate::piece::{Color, Piece};
use crate::piece::Piece::*;

pub fn movability_coeff(board: &[[Piece;8];8], movabs: &[[u8;8];8], move_num: usize) -> i32 {
    let mut coeff = 0i32;
    for r in 0..8 {
        for f in 0..8 {
            match board[r][f] {
                Piece::Empty => {}
                WKing => coeff += movabs[r][f] as i32,
                WQueen => coeff += min(movabs[r][f] as i32, move_num as i32 / 2),
                WRook => coeff += movabs[r][f] as i32,
                WBishop => coeff += {
                    let m = movabs[r][f] as i32;
                    if m == 0 { -8 } else { m }
                },
                WKnight => coeff += movabs[r][f] as i32,
                WPawn => coeff += {
                    let m = movabs[r][f] as i32;
                    if r == 1 && m == 0 { [-5, -4, -2, -15, -15, 0, -4, -5][f] } else { m }
                },
                BKing => coeff -= movabs[r][f] as i32,
                BQueen => coeff -= min(movabs[r][f] as i32, move_num as i32 / 2),
                BRook => coeff -= movabs[r][f] as i32,
                BBishop => coeff -= {
                    let m = movabs[r][f] as i32;
                    if m == 0 { -8 } else { m }
                },
                BKnight => coeff -= movabs[r][f] as i32,
                BPawn => coeff -= {
                    let m = movabs[r][f] as i32;
                    if r == 6 && m == 0 { [-5, -4, -2, -15, -15, 0, -4, -5][f] } else { m }
                },
            }
        }
    }
    coeff
}

pub fn movabilities(board: &[[Piece; 8]; 8]) -> [[u8; 8]; 8] {
    let mut movabs = [[0;8];8];
    for rank in 0..8 {
        for file in 0..8 {
            let piece = board[rank][file];
            let sq = &Square { rank: rank as u8, file: file as u8 };
            movabs[rank][file] = match piece {
                Piece::Empty => 0,
                WKing => tally_sing_movab(board, sq, White, [[1,1],[0,1],[-1,1],[-1,0],[-1,-1],[0,-1],[1,-1],[1,0]].iter()),
                WQueen => tally_cont_movab(board, sq, White, [[1,1],[0,1],[-1,1],[-1,0],[-1,-1],[0,-1],[1,-1],[1,0]].iter()),
                WRook => tally_cont_movab(board, sq, White, [[0,1],[-1,0],[0,-1],[1,0]].iter()),
                WBishop => tally_cont_movab(board, sq, White, [[1,1],[-1,1],[-1,-1],[1,-1]].iter()),
                WKnight => tally_sing_movab(board, sq, White, [[2,1],[1,2],[-2,1],[1,-2],[2,-1],[-1,2],[-2,-1],[-1,-2]].iter()),
                WPawn => white_pawn_movab(board, sq),
                BKing => tally_sing_movab(board, sq, Black, [[1,1],[0,1],[-1,1],[-1,0],[-1,-1],[0,-1],[1,-1],[1,0]].iter()),
                BQueen => tally_cont_movab(board, sq, Black, [[1,1],[0,1],[-1,1],[-1,0],[-1,-1],[0,-1],[1,-1],[1,0]].iter()),
                BRook => tally_cont_movab(board, sq, Black, [[0,1],[-1,0],[0,-1],[1,0]].iter()),
                BBishop => tally_cont_movab(board, sq, Black, [[1,1],[-1,1],[-1,-1],[1,-1]].iter()),
                BKnight => tally_sing_movab(board, sq, Black, [[2,1],[1,2],[-2,1],[1,-2],[2,-1],[-1,2],[-2,-1],[-1,-2]].iter()),
                BPawn => black_pawn_movab(board, sq),
            }
        }
    }

    movabs
}

fn white_pawn_movab(board: &[[Piece; 8]; 8], sq: &Square) -> u8 {
    if sq.rank == 7 { return 10; } // probably a queen
    let mut tot = 0;
    if sq.file > 0 && board[sq.rank as usize + 1][sq.file as usize - 1].color() == Black { tot += 1; }
    if sq.file < 7 && board[sq.rank as usize + 1][sq.file as usize + 1].color() == Black { tot += 1; }
    if board[sq.rank as usize + 1][sq.file as usize] != Piece::Empty {
        return tot;
    }
    if sq.rank == 1 && board[3][sq.file as usize] == Piece::Empty {
        tot += 1;
    }
    tot + 1
}

fn black_pawn_movab(board: &[[Piece; 8]; 8], sq: &Square) -> u8 {
    if sq.rank == 0 { return 10; } // probably a queen
    let mut tot = 0;
    if sq.file > 0 && board[sq.rank as usize - 1][sq.file as usize - 1].color() == White { tot += 1; }
    if sq.file < 7 && board[sq.rank as usize - 1][sq.file as usize + 1].color() == White { tot += 1; }
    if board[sq.rank as usize - 1][sq.file as usize] != Piece::Empty {
        return tot;
    }
    if sq.rank == 6 && board[4][sq.file as usize] == Piece::Empty {
        tot += 1;
    }
    tot + 1
}

fn tally_cont_movab<'a, I>(board: &[[Piece; 8]; 8], sq: &Square, color_moving: Color, offsets: I) -> u8
    where I: Iterator<Item=&'a[i8; 2]> {
    let mut tally = 0u8;
    for offset in offsets {
        let mut sq = [sq.rank as i8, sq.file as i8];
        sq[0] += offset[0]; sq[1] += offset[1];
        if !on_board(sq) { continue; }
        while on_board(sq) {
            let piece_hit = board[sq[0]as usize][sq[1]as usize];
            if piece_hit != Piece::Empty {
                if piece_hit.color() == color_moving.switch() { tally += 1; }
                break;
            }
            tally += 1;
            sq[0] += offset[0]; sq[1] += offset[1];
        }
    }

    tally
}

//not for pawns
fn tally_sing_movab<'a, I>(board: &[[Piece; 8]; 8], sq: &Square, color_moving: Color, offsets: I) -> u8
    where I: Iterator<Item=&'a[i8; 2]> {
    let mut tally = 0u8;
    for offset in offsets {
        let mut sq = [sq.rank as i8, sq.file as i8];
        sq[0] += offset[0]; sq[1] += offset[1];
        if !on_board(sq) { continue; }
        let piece_hit = board[sq[0]as usize][sq[1]as usize];
        if piece_hit == Piece::Empty || piece_hit.color() == color_moving.switch() {
            tally += 1;
        }
    }
    tally
}