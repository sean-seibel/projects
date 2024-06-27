import random

TRIALS = 10000

PEOPLE = 23
YEAR = 365

birthdates = [False] * YEAR

def shared_bday():
    for i in range(YEAR):
        birthdates[i] = False
    for _ in range(PEOPLE):
        bday = random.randint(0, YEAR - 1)
        if birthdates[bday]:
            # print(f"Shared birthday: day {bday}")
            return bday
        birthdates[bday] = True
    return None

def main():
    found_shared = 0
    for _ in range(TRIALS):
        if shared_bday() is not None:
            found_shared += 1
    print(f"Results: {found_shared} out of {TRIALS}")
    prob_all_unique = 1.0
    for i in range(PEOPLE):
        prob_all_unique *= (YEAR - i)
        prob_all_unique /= YEAR
    print(f"Expected: {TRIALS * (1 - prob_all_unique):.3f} out of {TRIALS}")
    

if __name__=="__main__":
    main()