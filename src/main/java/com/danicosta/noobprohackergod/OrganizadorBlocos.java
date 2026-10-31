
package com.danicosta.noobprohackergod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class OrganizadorBlocos {

    private OrganizadorBlocos() {
    }

    public static List<BlocoConstrucao> organizar(
            ProjetoConstrucao projeto
    ) {
        Objects.requireNonNull(
                projeto,
                "O projeto nao pode ser nulo"
        );

        List<BlocoConstrucao> resultado =
                new ArrayList<>(projeto.getBlocos());

        resultado.sort(
                Comparator
                        .comparingInt(BlocoConstrucao::getY)
                        .thenComparingInt(BlocoConstrucao::getZ)
                        .thenComparingInt(BlocoConstrucao::getX)
        );

        return resultado;
    }

    public static boolean estaOrganizado(
            List<BlocoConstrucao> blocos
    ) {
        Objects.requireNonNull(
                blocos,
                "A lista nao pode ser nula"
        );

        for (int i = 1; i < blocos.size(); i++) {
            BlocoConstrucao anterior = blocos.get(i - 1);
            BlocoConstrucao atual = blocos.get(i);

            if (anterior.getY() > atual.getY()) {
                return false;
            }

            if (anterior.getY() == atual.getY()
                    && anterior.getZ() > atual.getZ()) {
                return false;
            }

            if (anterior.getY() == atual.getY()
                    && anterior.getZ() == atual.getZ()
                    && anterior.getX() > atual.getX()) {
                return false;
            }
        }

        return true;
    }
}
