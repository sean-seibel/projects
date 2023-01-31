use crate::board::Square;
use crate::{in_endgame, White};
use crate::piece::{Piece, PieceType};
use crate::piece::Piece::*;

// in centipawns (not millipawns)
pub fn pst_analysis(board: &[[Piece;8];8]) -> i32 {
    let endgame = in_endgame(board) as i32;
    let midgame = (100 - endgame) as i32;
    let mut piece_enjoyment_index = 0;
    for r in 0..8 {
        for f in 0..8 {
            piece_enjoyment_index += match board[r][f] {
                Empty => 0,
                WKing => (endgame * WKING_END[r][f] + midgame * WKING_MID[r][f]) / 100,
                WQueen => WQUEEN[r][f],
                WRook => WROOK[r][f],
                WBishop => WBISHOP[r][f],
                WKnight => WKNIGHT[r][f],
                WPawn => WPAWN[r][f],
                BKing => -(endgame * WKING_END[7-r][f] + midgame * WKING_MID[7-r][f]) / 100,
                BQueen => -WQUEEN[7-r][f],
                BRook => -WROOK[7-r][f],
                BBishop => -WBISHOP[7-r][f],
                BKnight => -WKNIGHT[7-r][f],
                BPawn => -WPAWN[7-r][f],
            }
        }
    }

    piece_enjoyment_index
}

pub fn pst_board(board: &[[Piece;8];8]) -> [[i32;8];8] {
    let mut pstb = [[0i32;8];8];
    let endgame = in_endgame(board);
    let midgame = 100 - endgame;
    for r in 0..8 {
        for f in 0..8 {
            pstb[r][f] = match board[r][f] {
                Empty => 0,
                WKing => (endgame * WKING_END[r][f] + midgame * WKING_MID[r][f]) / 100,
                WQueen => WQUEEN[r][f],
                WRook => WROOK[r][f],
                WBishop => WBISHOP[r][f],
                WKnight => WKNIGHT[r][f],
                WPawn => WPAWN[r][f],
                BKing => (endgame * WKING_END[7-r][f] + midgame * WKING_MID[7-r][f]) / 100,
                BQueen => WQUEEN[7-r][f],
                BRook => WROOK[7-r][f],
                BBishop => WBISHOP[7-r][f],
                BKnight => WKNIGHT[7-r][f],
                BPawn => WPAWN[7-r][f],
            }
        }
    }
    pstb
}

impl Piece {
    pub fn happiness_at(&self, s: Square, endgame: i32) -> i32 {
        let r = if self.color()==White{s.rank}else{7-s.rank} as usize;
        match self.piece_type() {
            PieceType::Empty => 0,
            PieceType::King => {
                let midgame = 100 - endgame;
                (endgame * WKING_END[r][s.file as usize] + midgame * WKING_MID[r][s.file as usize]) / 100
            },
            PieceType::Queen => WQUEEN[r][s.file as usize],
            PieceType::Rook => WROOK[r][s.file as usize],
            PieceType::Bishop => WBISHOP[r][s.file as usize],
            PieceType::Knight => WKNIGHT[r][s.file as usize],
            PieceType::Pawn => WPAWN[r][s.file as usize],
        }
    }
}

// in centipawns (not millipawns)
const WPAWN: [[i32;8];8] =
[[0,  0,  0,  0,  0,  0,  0,  0],
[5, 15, 15,-50,-50, 10, 10,  5,],
[5, -5,-10,  0,  0,-0, -5,  5,],
[0,  0,  0, 40, 40,  20,  0,  0,],
[5,  5, 10, 30, 30, 10,  5,  5,],
[10, 10, 20, 30, 30, 20, 10, 10,],
[50, 50, 50, 50, 50, 50, 50, 50,],
[0,  0,  0,  0,  0,  0,  0,  0,]]; //

const WKNIGHT: [[i32;8];8] =
[[-50,-40,-30,-30,-30,-30,-40,-50],
[-40,-20,  0,  0,  0,  0,-20,-40],
[-30,  5, 30, 15, 15, 30,  5,-30],
[-30,  0, 15, 20, 20, 15,  0,-30],
[-30,  5, 15, 20, 20, 15,  5,-30],
[-30,  0, 10, 15, 15, 10,  0,-30],
[-40,-20,  0,  5,  5,  0,-20,-40],
[-50,-40,-30,-30,-30,-30,-40,-50]];

const WBISHOP: [[i32;8];8] =
[[-20,-10,-10,-10,-10,-10,-10,-20],
[-10,  5,  0,  0,  0,  0,  5,-10],
[-10, 10, 10, 10, 10, 10, 10,-10],
[-10,  0, 10, 10, 10, 10,  0,-10],
[-10,  5,  5, 10, 10,  5,  5,-10],
[-10,  0,  5, 10, 10,  5,  0,-10],
[-10,  0,  0,  0,  0,  0,  0,-10],
[-20,-10,-10,-10,-10,-10,-10,-20]];

const WROOK: [[i32;8];8] =
[[0,  0,  5,  10,  10,  5,  0,  0],
[-5,  0,  0,  0,  0,  0,  0, -5],
[-5,  0,  0,  0,  0,  0,  0, -5],
[-5,  0,  0,  0,  0,  0,  0, -5],
[-5,  0,  0,  0,  0,  0,  0, -5],
[-5,  0,  0,  0,  0,  0,  0, -5],
[5, 15, 15, 15, 15, 15, 15,  5],
[0,  0,  0,  0,  0,  0,  0,  0],
];

const WQUEEN: [[i32;8];8] =
[[-20,-10,-10, -5, -5,-10,-10,-20],
[-10,  0,  5,  0,  0,  0,  0,-10],
[-10,  0,  5,  5,  5,  5,  0,-10],
[0,  0,  5,  5,  5,  5,  0, -5],
[-5,  0,  5,  5,  5,  5,  0, -5],
[-10,  5,  5,  5,  5,  5,  0,-10],
[-10,  0,  0,  0,  0,  0,  0,-10],
[-20,-10,-10, -5, -5,-10,-10,-20]];

const WKING_MID: [[i32;8];8] =
[[20, 50, 10,  0,  0, 30, 20, 20],
[20, 20,  0,  -20,  -20,  0, 20, 20],
[-10,-20,-20,-20,-20,-20,-20,-10],
[-20,-30,-30,-40,-40,-30,-30,-20],
[-30,-40,-40,-50,-50,-40,-40,-30],
[-30,-40,-40,-50,-50,-40,-40,-30],
[-30,-40,-40,-50,-50,-40,-40,-30],
[-30,-40,-40,-50,-50,-40,-40,-30]];

const WKING_END: [[i32;8];8] =
[[-50,-30,-30,-30,-30,-30,-30,-50],
[-30,-30,  0,  0,  0,  0,-30,-30],
[-30,-10, 20, 30, 30, 20,-10,-30],
[-30,-10, 30, 40, 40, 30,-10,-30],
[-30,-10, 30, 40, 40, 30,-10,-30],
[-30,-10, 20, 30, 30, 20,-10,-30],
[-30,-20,-10,  0,  0,-10,-20,-30],
[-50,-40,-30,-20,-20,-30,-40,-50]];
