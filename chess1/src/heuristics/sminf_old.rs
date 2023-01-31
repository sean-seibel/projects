use std::cmp::min_by;
use crate::board::{on_board, Square};
use crate::piece::{Color, Piece};
use crate::piece::Piece::*;
use crate::{Black, White};
use crate::piece::PieceType::{Bishop, Pawn, Queen, Rook};

pub fn sminf_coeff(board: &[[Piece; 8]; 8], sminfs: &[[i32;8];8], to_move: Color) -> i32 {
    let mut sum = 0i32;
    let mut w_attacked: Vec<i32> = Vec::with_capacity(3);
    let mut b_attacked: Vec<i32> = Vec::with_capacity(3);
    for r in 0..8 {
        for f in 0..8 {
            let piece = board[r][f];
            //let mat = if piece.piece_type() == King { 3000 }
            match piece.color() {
                Color::None => sum += 1,
                White => if sminfs[r][f] < 0 { w_attacked.push(piece.material_eval(r as u8, f as u8)) }
                Black => if sminfs[r][f] > 0 { b_attacked.push(piece.material_eval(r as u8, f as u8)) }
            }
        }
    }
    if to_move==White{
        w_attacked.sort();
        w_attacked.pop();
    } else {
        b_attacked.sort();
        b_attacked.pop();
    }
    b_attacked.iter().fold(0, |acc, x| acc + x) - w_attacked.iter().fold(0, |acc, x| acc + x)
}

pub fn smart_influence_grid(board: &[[Piece; 8]; 8]) -> [[i32; 8]; 8] {
    let (winfs, binfs) = smart_influence_grid_double(board);
    let mut res = [[0i32;8];8];
    for r in 0..8 {
        for f in 0..8 {
            res[r][f] += winfs[r][f] + binfs[r][f];
        }
    }
    res
}

pub fn smart_influence_grid_double(board: &[[Piece; 8]; 8]) -> ([[i32; 8]; 8],[[i32; 8]; 8]) {
    let mut winfs = [[0i32; 8]; 8];
    let mut binfs = [[0i32; 8]; 8];

    for rank in 0u8..8 {
        for file in 0u8..8 {
            let piece = board[rank as usize][file as usize];
            let sq = &Square { rank, file };
            match piece {
                Piece::Empty => {},
                WKing => populate_sing_offsets_sm(&mut winfs, piece.sminf(), [[1,1],[0,1],[-1,1],[-1,0],[-1,-1],[0,-1],[1,-1],[1,0]].iter(), sq),
                WQueen => { populate_orth_offsets_sm(&mut winfs, board, piece.sminf(), sq, White);
                    populate_diag_offsets_sm(&mut winfs, board, piece.sminf(), sq, White);
                },
                WRook => populate_orth_offsets_sm(&mut winfs, board, piece.sminf(), sq, White),
                WBishop => populate_diag_offsets_sm(&mut winfs, board, piece.sminf(), sq, White),
                WKnight => populate_sing_offsets_sm(&mut winfs, piece.sminf(), [[2,1],[1,2],[-2,1],[1,-2],[2,-1],[-1,2],[-2,-1],[-1,-2]].iter(), sq),
                WPawn => populate_sing_offsets_sm(&mut winfs, piece.sminf(), [[1, 1], [1, -1]].iter(), sq),
                BKing => populate_sing_offsets_sm(&mut binfs, piece.sminf(), [[1,1],[0,1],[-1,1],[-1,0],[-1,-1],[0,-1],[1,-1],[1,0]].iter(), sq),
                BQueen => { populate_orth_offsets_sm(&mut binfs, board, piece.sminf(), sq, Black);
                    populate_diag_offsets_sm(&mut binfs, board, piece.sminf(), sq, Black);
                },
                BRook => populate_orth_offsets_sm(&mut binfs, board, piece.sminf(), sq, Black),
                BBishop => populate_diag_offsets_sm(&mut binfs, board, piece.sminf(), sq, Black),
                BKnight => populate_sing_offsets_sm(&mut binfs, piece.sminf(), [[2,1],[1,2],[-2,1],[1,-2],[2,-1],[-1,2],[-2,-1],[-1,-2]].iter(), sq),
                BPawn => populate_sing_offsets_sm(&mut binfs, piece.sminf(), [[-1, 1], [-1, -1]].iter(), sq),
            }
        }
    }

    (winfs, binfs)
}

// add x-raying
fn populate_orth_offsets_sm(infs: &mut [[i32;8];8], board: &[[Piece; 8]; 8], diff: i32, sq: &Square, color: Color) {
    let cont_if = [Piece::Empty, Rook.of_color(color), Queen.of_color(color)];
    for offset in [[1,0],[0,1],[-1,0],[0,-1]] {
        let mut diff = diff;
        let mut sq = [sq.rank as i8, sq.file as i8];
        sq[0] += offset[0]; sq[1] += offset[1];
        if !on_board(sq) { continue; }
        while on_board(sq) {
            infs[sq[0]as usize][sq[1]as usize] += diff;
            let piece = board[sq[0]as usize][sq[1]as usize];
            if !cont_if.contains(&piece) { break; }
            if piece != Piece::Empty { diff = min_by(diff, piece.sminf(), |x: &i32, y: &i32| x.abs().cmp(&y.abs())) }
            sq[0] += offset[0]; sq[1] += offset[1];
        }
    }
}

fn populate_diag_offsets_sm(infs: &mut [[i32;8];8], board: &[[Piece; 8]; 8], diff: i32, sq: &Square, color: Color) {
    let cont_if = [Piece::Empty, Bishop.of_color(color), Queen.of_color(color)];
    let pawn_continuations = if color==White { [[1i8, 1], [1, -1]] } else { [[-1, 1], [-1, -1]] };
    for offset in [[1,1],[-1,1],[1,-1],[-1,-1]] {
        let mut diff = diff;
        let mut sq = [sq.rank as i8, sq.file as i8];
        sq[0] += offset[0]; sq[1] += offset[1];
        if !on_board(sq) { continue; }
        while on_board(sq) {
            infs[sq[0]as usize][sq[1]as usize] += diff;
            let piece = board[sq[0]as usize][sq[1]as usize];
            if !cont_if.contains(&piece) {
                if on_board([sq[0]+offset[0],sq[1]+offset[1]]) && board[sq[0]as usize][sq[1]as usize] == Pawn.of_color(color) && pawn_continuations.contains(&offset)  {
                    infs[(sq[0]+offset[0])as usize][(sq[1]+offset[1])as usize] += diff
                }
                break;
            }
            if piece != Piece::Empty { diff = min_by(diff, piece.sminf(), |x: &i32, y: &i32| x.abs().cmp(&y.abs())) }
            sq[0] += offset[0]; sq[1] += offset[1];
        }
    }
}

fn populate_sing_offsets_sm<'a, I>(infs: &mut [[i32;8];8], diff: i32, offsets: I, sq: &Square)
    where I: Iterator<Item=&'a[i8; 2]>
{
    for offset in offsets {
        let sq = [sq.rank as i8 + offset[0] , sq.file as i8 + offset[1]];
        if !on_board(sq) { continue; }
        infs[sq[0]as usize][sq[1]as usize] += diff;
    }
}

impl Piece {
    pub(crate) fn sminf(&self) -> i32 {
        match self {
            Piece::Empty => 0,
            WKing => 1,
            WQueen => 4,
            WRook => 16,
            WBishop => 64,
            WKnight => 64,
            WPawn => 512,
            BKing => -1,
            BQueen => -4,
            BRook => -16,
            BBishop => -64,
            BKnight => -64,
            BPawn => -512
        }
    }
}