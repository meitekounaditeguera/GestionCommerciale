package com.gestioncommerciale.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class MontantFcfaTest {

    @Test
    void doitFormaterVenteEtAchatDeLaMemeFacon() {
        // Vente : prix relus en base avec 2 décimales. Achat : prix saisis sans décimales.
        assertThat(MontantFcfa.formater(new BigDecimal("400000.00"))).isEqualTo("400 000 FCFA");
        assertThat(MontantFcfa.formater(new BigDecimal("1500000"))).isEqualTo("1 500 000 FCFA");
    }

    @Test
    void doitArrondirEtGererLesPetitsMontants() {
        assertThat(MontantFcfa.formater(new BigDecimal("999.50"))).isEqualTo("1 000 FCFA");
        assertThat(MontantFcfa.formater(new BigDecimal("0.00"))).isEqualTo("0 FCFA");
        assertThat(MontantFcfa.formater(new BigDecimal("-2500"))).isEqualTo("-2 500 FCFA");
    }
}
