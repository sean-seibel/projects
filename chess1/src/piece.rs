use crate::board::Square;
use crate::piece::Color::{Black, White};

#[derive(Copy, Clone)]
#[derive(Eq, PartialEq)]
pub enum Piece {
    Empty,
    WKing,
    WQueen,
    WRook,
    WBishop,
    WKnight,
    WPawn,
    BKing,
    BQueen,
    BRook,
    BBishop,
    BKnight,
    BPawn,
}

#[derive(Copy, Clone)]
#[derive(Eq, PartialEq)]
pub enum PieceType {
    Empty,
    King,
    Queen,
    Rook,
    Bishop,
    Knight,
    Pawn,
}

#[derive(Copy, Clone)]
#[derive(Eq, PartialEq)]
#[derive(Debug)]
pub enum Color {
    None,
    White,
    Black,
}

impl Color {
    pub(crate) fn switch(&self) -> Color {
        match self {
            Color::None => panic!("Can't switch none"),
            White => Black,
            Black => White,
        }
    }
}

impl Piece {
    pub fn color(&self) -> Color {
        return match self {
            Piece::Empty => Color::None,
            Piece::WKing => White,
            Piece::WQueen => White,
            Piece::WRook => White,
            Piece::WBishop => White,
            Piece::WKnight => White,
            Piece::WPawn => White,
            Piece::BKing => Black,
            Piece::BQueen => Black,
            Piece::BRook => Black,
            Piece::BBishop => Black,
            Piece::BKnight => Black,
            Piece::BPawn => Black,
        }
    }

    pub fn piece_type(&self) -> PieceType {
        return match self {
            Piece::Empty => PieceType::Empty,
            Piece::WKing => PieceType::King,
            Piece::WQueen => PieceType::Queen,
            Piece::WRook => PieceType::Rook,
            Piece::WBishop => PieceType::Bishop,
            Piece::WKnight => PieceType::Knight,
            Piece::WPawn => PieceType::Pawn,
            Piece::BKing => PieceType::King,
            Piece::BQueen => PieceType::Queen,
            Piece::BRook => PieceType::Rook,
            Piece::BBishop => PieceType::Bishop,
            Piece::BKnight => PieceType::Knight,
            Piece::BPawn => PieceType::Pawn,
        }
    }

    pub fn char(&self) -> &str {
        return match self {
            Piece::Empty => ".",
            Piece::WKing => "♔",
            Piece::WQueen => "♕",
            Piece::WRook => "♖",
            Piece::WBishop => "♗",
            Piece::WKnight => "♘",
            Piece::WPawn => "♙",
            Piece::BKing => "♚",
            Piece::BQueen => "♛",
            Piece::BRook => "♜",
            Piece::BBishop => "♝",
            Piece::BKnight => "♞",
            Piece::BPawn => "♟",// '\u{2398}',
        }
    }
}

impl PieceType {
    pub fn of_color(&self, color: Color) -> Piece {
        match color {
            Color::None => Piece::Empty,
            White => match self {
                PieceType::Empty => Piece::Empty,
                PieceType::King => Piece::WKing,
                PieceType::Queen => Piece::WQueen,
                PieceType::Rook => Piece::WRook,
                PieceType::Bishop => Piece::WBishop,
                PieceType::Knight => Piece::WKnight,
                PieceType::Pawn => Piece::WPawn,
            }
            Black => match self {
                PieceType::Empty => Piece::Empty,
                PieceType::King => Piece::BKing,
                PieceType::Queen => Piece::BQueen,
                PieceType::Rook => Piece::BRook,
                PieceType::Bishop => Piece::BBishop,
                PieceType::Knight => Piece::BKnight,
                PieceType::Pawn => Piece::BPawn,
            }
        }
    }
}

pub const WQUEENSIDE: u8 = 1;
pub const WKINGSIDE: u8 = 2;
pub const BQUEENSIDE: u8 = 4;
pub const BKINGSIDE: u8 = 8;

pub enum GameOver {
    NotOver,
    Checkmate(Color), // color is the loser who is checkmated
    Draw,
}

trait At {
    type I: Sized;
    fn at(&self, sq: Square) -> Self::I;
}

impl<T> At for [[T; 8]; 8] where T: Sized + Copy {
    type I = T;

    fn at(&self, sq: Square) -> Self::I {
        self[sq.rank as usize][sq.file as usize]
    }
}