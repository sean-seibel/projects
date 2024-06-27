#include <stdio.h>

const int eight_bit_high_bits[256] = {
    0,1,2,2,3,3,3,3,4,4,4,4,4,4,4,4,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8
};
int highest_bit_64_super_index(unsigned long n) {
    int index = 0;
    if (n >> (index + 32)) {
        index += 32;
    }
    if (n >> (index + 16)) {
        index += 16;
    }
    if (n >> (index + 8)) {
        index += 8;
    }
    return index + eight_bit_high_bits[n >> index];
}

const int four_bit_high_bits[16] = {0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4};
int highest_bit_64(unsigned long n) {
    int index = 0;
    if (n >> (index + 32)) {
        index += 32;
    }
    if (n >> (index + 16)) {
        index += 16;
    }
    if (n >> (index + 8)) {
        index += 8;
    }
    if (n >> (index + 4)) {
        index += 4;
    }
    return index + four_bit_high_bits[n >> index];
}

int highest_bit_64_full_loop(unsigned long n) {
    int index = 0;
    if (n >> (index + 32)) {
        index += 32;
    }
    if (n >> (index + 16)) {
        index += 16;
    }
    if (n >> (index + 8)) {
        index += 8;
    }
    if (n >> (index + 4)) {
        index += 4;
    }
    if (n >> (index + 2)) {
        index += 2;
    }
    if (n >> (index + 1)) {
        index += 1;
    }
    return index + (n >> index);
}

int highest_bit_64_full_loop_rolled_up(unsigned long n) {
    int index = 0;
    for (int width = 32; width; width >>= 1) {
        if (n >> (index + width)) {
            index += width;
        }
    }
    return index + (n >> index); // this is kind of like indexing into a table for [0,1]. im pretty sure this is just 1 for everything except 0
}


int main() {
    // int ans;
    // for (long i = 0; i <= 5000000000; i++)
    //     ans ^= highest_bit_64_super_index(i);
    // printf("%i\n", ans);
    for (long i = 0; i < 30; i++) {
        printf("%i,", highest_bit_64_full_loop(i));
    }
    printf("\n");
    for (long i = 0; i < 30; i++) {
        printf("%i,", highest_bit_64_full_loop_rolled_up(i));
    }
    printf("\n");
    for (long i = 0; i < 30; i++) {
        printf("%i,", highest_bit_64(i));
    }
    printf("\n");
    for (long i = 0; i < 30; i++) {
        printf("%i,", highest_bit_64_super_index(i));
    }
    return 0;
}