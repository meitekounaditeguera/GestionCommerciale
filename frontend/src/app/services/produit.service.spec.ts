import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { ProduitService } from './produit.service';
import { Produit } from '../models/produit';
import { Page } from '../models/page';

describe('ProduitService', () => {
  let service: ProduitService;
  let httpMock: HttpTestingController;

  const apiUrl = 'http://localhost:8080/api/produits';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(ProduitService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('envoie une requête GET paginée pour récupérer les produits', () => {
    const pageSimulee: Page<Produit> = {
      content: [],
      totalElements: 0,
      totalPages: 1,
      number: 0,
      size: 5,
    };

    service.getProduits(0, 5).subscribe((reponse) => {
      expect(reponse).toEqual(pageSimulee);
    });

    const requete = httpMock.expectOne((r) => r.url === apiUrl && r.method === 'GET');
    expect(requete.request.params.get('page')).toBe('0');
    expect(requete.request.params.get('size')).toBe('5');
    requete.flush(pageSimulee);
  });

  it('envoie une requête POST avec le produit en corps de requête pour en ajouter un', () => {
    const nouveauProduit: Produit = { nom: 'Clé USB', description: '', prix: 5000, quantite: 10 };
    const produitCree: Produit = { ...nouveauProduit, id: 1 };

    service.addProduit(nouveauProduit).subscribe((reponse) => {
      expect(reponse).toEqual(produitCree);
    });

    const requete = httpMock.expectOne({ url: apiUrl, method: 'POST' });
    expect(requete.request.body).toEqual(nouveauProduit);
    requete.flush(produitCree);
  });

  it('envoie une requête PUT vers /api/produits/{id} pour modifier un produit', () => {
    const produitModifie: Produit = { nom: 'Clé USB 128Go', description: '', prix: 6000, quantite: 8 };

    service.updateProduit(5, produitModifie).subscribe((reponse) => {
      expect(reponse).toEqual({ ...produitModifie, id: 5 });
    });

    const requete = httpMock.expectOne({ url: `${apiUrl}/5`, method: 'PUT' });
    expect(requete.request.body).toEqual(produitModifie);
    requete.flush({ ...produitModifie, id: 5 });
  });

  it('envoie une requête DELETE vers /api/produits/{id} pour supprimer un produit', () => {
    service.deleteProduit(42).subscribe();

    const requete = httpMock.expectOne({ url: `${apiUrl}/42`, method: 'DELETE' });
    requete.flush(null);
  });

  it('envoie une requête GET avec le code en paramètre pour la recherche par code-barres', () => {
    const produitTrouve: Produit = {
      id: 3,
      nom: 'Chargeur',
      description: '',
      prix: 3000,
      quantite: 5,
      codeBarre: '123456',
    };

    service.rechercherParCodeBarre('123456').subscribe((reponse) => {
      expect(reponse).toEqual(produitTrouve);
    });

    const requete = httpMock.expectOne((r) => r.url === `${apiUrl}/recherche/code-barre`);
    expect(requete.request.method).toBe('GET');
    expect(requete.request.params.get('code')).toBe('123456');
    requete.flush(produitTrouve);
  });
});
