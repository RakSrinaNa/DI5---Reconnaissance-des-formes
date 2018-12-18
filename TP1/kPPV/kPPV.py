""" programme à compléter du kPPV"""
import csv
from math import sqrt

nbExParClasse = 50
nbApprent = 25
nbCaract = 4
nbClasse = 3


def lecture_fichier_csv():
    with open("iris.data", 'r')as fic:
        lines = csv.reader(fic)
        dataset = list(lines)
    for i in range(len(dataset)):
        for j in range(nbCaract):
            dataset[i][j] = float(dataset[i][j])
    return dataset


def calcul_distances(data, dataset):
    """ retourne les distances entre data et la partie apprentissage de dataset"""
    distances = []

    for c in range(0, nbClasse):
        stbd = dataset[c*nbExParClasse:c*nbExParClasse+nbApprent]
        for a in stbd:
            s = 0
            for i in range(0, nbCaract):
                s += (data[i] - a[i])**2
            distances.append((sqrt(s), c))

    return distances


def calcul_classe(distances, k):
    """ retourne le numéro de la classe déterminé à partir des distances """
    k = max(0, min(nbApprent * nbClasse, k))
    l = sorted(distances, key=lambda tup: tup[0])[:k]

    r = [0 for i in range(0, nbClasse)]

    for v in l:
        r[v[1]] += 1

    candidates = [i for i, j in enumerate(r) if j == max(r)]
    if len(candidates) > 1:
        b = [min([j[0] for j in l if j[1] == i]) for i in candidates]
        best_candidate = [i for i, j in enumerate(b) if j == min(b)][0]
        return candidates[best_candidate]
    return candidates[0]


def calcul_taux(mat):
    s = 0
    t = 0
    for i in range(0, len(mat)):
        s += mat[i][i]
        for j in range(0, len(mat)):
            t += mat[i][j]
    return s/t


if __name__ == "__main__":
    print("Début programme kPPV")
    dataset = lecture_fichier_csv()

    matrix = [[0 for j in range(0, nbClasse)] for i in range(0, nbClasse)]
    for c in range(0, nbClasse):
        subdata = dataset[c*nbExParClasse+nbApprent:(c+1)*nbExParClasse]
        for a in subdata:
            classe = calcul_classe(calcul_distances(a, dataset), 2)
            matrix[c][classe] += 1

    for l in matrix:
        print(str(l))
    print(calcul_taux(matrix))
