use crate::piece::{Color, Piece, PieceType};
use crate::piece::Color::{White, Black};
use crate::piece::Piece::*;
use crate::piece::PieceType::*;

pub fn material_count(board: &[[Piece; 8]; 8]) -> i32 {
    let mut white_total = 0i32;
    let mut black_total = 0i32;

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

impl Piece {
    //always pos
    pub(crate) fn material_eval(&self, rank: u8, file: u8) -> i32 {
        self.material_eval_base() + match self {
            WPawn => match rank {
                6 => 2000,
                5 => 300,
                3 => 100,
                _ => 000,
            },
            BPawn => match rank {
                1 => 2000,
                2 => 300,
                4 => 100,
                _ => 000,
            },
            _ => 0
        }
    }

    pub(crate) fn material_eval_base(&self) -> i32 {
        match self.piece_type() {
            King => 200000,
            Queen => 10000,
            Rook => 5000,
            Bishop => 3050,
            Knight => 3000,
            Pawn => 1000,
            PieceType::Empty => 0
        }
    }
}