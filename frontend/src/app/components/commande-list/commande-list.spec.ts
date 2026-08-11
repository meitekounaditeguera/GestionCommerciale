import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommandeListComponent } from './commande-list';
import { Produit } from '../../models/produit';

describe('CommandeListComponent', () => {
  let component: CommandeListComponent;
  let fixture: ComponentFixture<CommandeListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommandeListComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CommandeListComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

// Logique de calcul pure (ajout de ligne, sous-total, montant total) : on instancie la classe
// directement plutôt que de passer par TestBed, puisque ngOnInit() n'est jamais appelé ici et
// que rien ne dépend du DOM. Les dépendances injectées ne sont jamais utilisées par les
// méthodes testées, donc de simples objets vides suffisent.
describe('CommandeListComponent - calculs', () => {
  let component: CommandeListComponent;

  const produitsDeTest: Produit[] = [
    { id: 1, nom: 'Clé USB 64Go', description: '', prix: 5000, quantite: 50 },
    { id: 2, nom: 'Souris HP', description: '', prix: 8000, quantite: 20 },
  ];

  beforeEach(() => {
    component = new CommandeListComponent(
      {} as any,
      {} as any,
      {} as any,
      {} as any,
      {} as any,
      {} as any,
      {} as any
    );
    component.produits = produitsDeTest;
  });

  describe('ajouterLigne', () => {
    it("ajoute une ligne à la commande lorsque le produit et la quantité sont valides", () => {
      component.nouvelleLigne = { produitId: 1, quantite: 3 };

      component.ajouterLigne();

      expect(component.nouvelleCommande.lignes).toEqual([{ produitId: 1, quantite: 3 }]);
    });

    it("réinitialise le formulaire de saisie après l'ajout d'une ligne", () => {
      component.nouvelleLigne = { produitId: 1, quantite: 3 };

      component.ajouterLigne();

      expect(component.nouvelleLigne).toEqual({ produitId: 0, quantite: 1 });
    });

    it("n'ajoute pas de ligne si aucun produit n'est sélectionné (produitId à 0)", () => {
      component.nouvelleLigne = { produitId: 0, quantite: 2 };

      component.ajouterLigne();

      expect(component.nouvelleCommande.lignes).toEqual([]);
    });

    it("n'ajoute pas de ligne si la quantité est à 0", () => {
      component.nouvelleLigne = { produitId: 1, quantite: 0 };

      component.ajouterLigne();

      expect(component.nouvelleCommande.lignes).toEqual([]);
    });

    it("n'ajoute pas de ligne si la quantité est négative", () => {
      component.nouvelleLigne = { produitId: 1, quantite: -5 };

      component.ajouterLigne();

      expect(component.nouvelleCommande.lignes).toEqual([]);
    });

    it('accumule plusieurs lignes ajoutées successivement', () => {
      component.nouvelleLigne = { produitId: 1, quantite: 2 };
      component.ajouterLigne();
      component.nouvelleLigne = { produitId: 2, quantite: 1 };
      component.ajouterLigne();

      expect(component.nouvelleCommande.lignes).toEqual([
        { produitId: 1, quantite: 2 },
        { produitId: 2, quantite: 1 },
      ]);
    });
  });

  describe('supprimerLigne', () => {
    afterEach(() => {
      vi.restoreAllMocks();
    });

    it('retire la ligne au bon index après confirmation', () => {
      vi.spyOn(window, 'confirm').mockReturnValue(true);
      component.nouvelleCommande.lignes = [
        { produitId: 1, quantite: 2 },
        { produitId: 2, quantite: 1 },
      ];

      component.supprimerLigne(0);

      expect(component.nouvelleCommande.lignes).toEqual([{ produitId: 2, quantite: 1 }]);
    });

    it('ne retire aucune ligne si la confirmation est annulée', () => {
      vi.spyOn(window, 'confirm').mockReturnValue(false);
      component.nouvelleCommande.lignes = [{ produitId: 1, quantite: 2 }];

      component.supprimerLigne(0);

      expect(component.nouvelleCommande.lignes).toEqual([{ produitId: 1, quantite: 2 }]);
    });
  });

  describe('getPrixProduit', () => {
    it("retourne le prix unitaire d'un produit existant", () => {
      expect(component.getPrixProduit(2)).toBe(8000);
    });

    it("retourne 0 si le produit n'existe pas dans le catalogue chargé", () => {
      expect(component.getPrixProduit(999)).toBe(0);
    });
  });

  describe('getNomProduit', () => {
    it("retourne le nom d'un produit existant", () => {
      expect(component.getNomProduit(1)).toBe('Clé USB 64Go');
    });

    it("retourne 'Produit inconnu' si le produit n'existe pas", () => {
      expect(component.getNomProduit(999)).toBe('Produit inconnu');
    });
  });

  describe('calculerMontantTotal', () => {
    it('retourne 0 pour une commande sans ligne', () => {
      expect(component.calculerMontantTotal()).toBe(0);
    });

    it('calcule le sous-total sur une seule ligne (prix unitaire × quantité)', () => {
      component.nouvelleCommande.lignes = [{ produitId: 1, quantite: 3 }];

      expect(component.calculerMontantTotal()).toBe(15000); // 5000 * 3
    });

    it('additionne les sous-totaux de plusieurs lignes', () => {
      component.nouvelleCommande.lignes = [
        { produitId: 1, quantite: 2 }, // 5000 * 2 = 10000
        { produitId: 2, quantite: 1 }, // 8000 * 1 = 8000
      ];

      expect(component.calculerMontantTotal()).toBe(18000);
    });

    it("ignore une ligne dont le produit n'existe plus dans le catalogue (prix compté comme 0)", () => {
      component.nouvelleCommande.lignes = [
        { produitId: 1, quantite: 2 }, // 5000 * 2 = 10000
        { produitId: 999, quantite: 5 }, // produit inconnu -> 0
      ];

      expect(component.calculerMontantTotal()).toBe(10000);
    });
  });
});
