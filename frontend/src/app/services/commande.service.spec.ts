import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { CommandeService } from './commande.service';
import { Commande } from '../models/commande';
import { Page } from '../models/page';

describe('CommandeService', () => {
  let service: CommandeService;
  let httpMock: HttpTestingController;

  const apiUrl = 'http://localhost:8080/api/commandes';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(CommandeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('envoie une requête GET paginée pour récupérer les commandes', () => {
    const pageSimulee: Page<Commande> = {
      content: [],
      totalElements: 0,
      totalPages: 1,
      number: 0,
      size: 5,
    };

    service.getCommandes(0, 5).subscribe((reponse) => {
      expect(reponse).toEqual(pageSimulee);
    });

    const requete = httpMock.expectOne((r) => r.url === apiUrl && r.method === 'GET');
    expect(requete.request.params.get('page')).toBe('0');
    expect(requete.request.params.get('size')).toBe('5');
    requete.flush(pageSimulee);
  });

  it('envoie une requête POST avec la commande en corps de requête pour en créer une', () => {
    const nouvelleCommande: Commande = {
      dateCommande: '2026-08-11',
      clientId: 1,
      lignes: [{ produitId: 1, quantite: 2 }],
    };
    const commandeCreee: Commande = { ...nouvelleCommande, id: 10, montantTotal: 10000 };

    service.addCommande(nouvelleCommande).subscribe((reponse) => {
      expect(reponse).toEqual(commandeCreee);
    });

    const requete = httpMock.expectOne({ url: apiUrl, method: 'POST' });
    expect(requete.request.body).toEqual(nouvelleCommande);
    requete.flush(commandeCreee);
  });

  it('envoie une requête DELETE vers /api/commandes/{id} pour supprimer une commande', () => {
    service.deleteCommande(7).subscribe();

    const requete = httpMock.expectOne({ url: `${apiUrl}/7`, method: 'DELETE' });
    requete.flush(null);
  });

  it("envoie une requête PUT vers /api/commandes/{id}/annuler pour annuler une commande", () => {
    const commandeAnnulee: Commande = {
      id: 7,
      dateCommande: '2026-08-11',
      clientId: 1,
      lignes: [],
      statut: 'ANNULE',
    };

    service.annulerCommande(7).subscribe((reponse) => {
      expect(reponse).toEqual(commandeAnnulee);
    });

    const requete = httpMock.expectOne({ url: `${apiUrl}/7/annuler`, method: 'PUT' });
    expect(requete.request.body).toEqual({});
    requete.flush(commandeAnnulee);
  });
});
