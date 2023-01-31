use rand::Rng;
use crate::{Board, influence_grid, movabilities, movability_coeff, material_count, pst_analysis, smart_influence_grid_double, tension_board, threat_value};
use crate::heuristics::inf_old::influence_coeff;

pub mod material_count;
pub mod movability_old;
pub mod sminf_old;
pub mod inf_old;
pub mod endgame_reached;
pub mod piece_square_tables;
pub mod tensity;
mod king_danger;

impl Board {
    pub fn immediate_analysis(&self) -> i32 {
        let board = &self.state;
        let movabs = &movabilities(board);
        return
            material_count(board) +
                12 * movability_coeff(board, movabs, self.move_count) +
                9 * pst_analysis(board);
    }

    pub fn analysis_with_imminent_attacks(&self) -> i32 {
        let board = &self.state;
        let movabs = &movabilities(board);
        let (winfs, binfs) = smart_influence_grid_double(board);
        let attack_board = tension_board(board, &winfs, &binfs);
        let threat_analysis = threat_value(board, attack_board, self.active_color);
        return
            material_count(board) +
                12 * movability_coeff(board, movabs, self.move_count) +
                9 * pst_analysis(board) +
                (threat_analysis * 9) / 10
        ;
    }

    pub fn simple_analysis(&self) -> i32 {
        let board = &self.state;
        return
            material_count(board) +
                7 * pst_analysis(board);
    }
}