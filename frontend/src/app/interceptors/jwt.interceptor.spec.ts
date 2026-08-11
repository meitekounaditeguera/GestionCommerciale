import { TestBed } from '@angular/core/testing';
import { HttpRequest, HttpHandlerFn } from '@angular/common/http';

import { jwtInterceptor } from './jwt.interceptor';
import { AuthService } from '../services/auth.service';

describe('jwtInterceptor', () => {
  let authServiceMock: { getToken: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    authServiceMock = { getToken: vi.fn() };

    TestBed.configureTestingModule({
      providers: [{ provide: AuthService, useValue: authServiceMock }],
    });
  });

  // Exécute l'intercepteur dans un contexte d'injection Angular (nécessaire car il appelle
  // inject(AuthService) en interne) et retourne la requête effectivement transmise à `next`.
  function requeteTransmiseA(request: HttpRequest<unknown>): HttpRequest<unknown> {
    const next = vi.fn().mockReturnValue('reponse-simulee') as unknown as HttpHandlerFn;

    TestBed.runInInjectionContext(() => jwtInterceptor(request, next));

    return (next as unknown as ReturnType<typeof vi.fn>).mock.calls[0][0];
  }

  it("ajoute l'en-tête Authorization pour une requête vers l'API backend lorsqu'un token existe", () => {
    authServiceMock.getToken.mockReturnValue('mon-token');
    const requete = new HttpRequest('GET', 'http://localhost:8080/api/clients');

    const requeteTransmise = requeteTransmiseA(requete);

    expect(requeteTransmise.headers.get('Authorization')).toBe('Bearer mon-token');
  });

  it("n'ajoute pas l'en-tête Authorization pour une requête vers une autre origine", () => {
    authServiceMock.getToken.mockReturnValue('mon-token');
    const requete = new HttpRequest('GET', 'https://autre-domaine.exemple.com/data');

    const requeteTransmise = requeteTransmiseA(requete);

    expect(requeteTransmise.headers.get('Authorization')).toBeNull();
  });

  it("n'ajoute pas l'en-tête Authorization si aucun token n'est stocké", () => {
    authServiceMock.getToken.mockReturnValue(null);
    const requete = new HttpRequest('GET', 'http://localhost:8080/api/clients');

    const requeteTransmise = requeteTransmiseA(requete);

    expect(requeteTransmise.headers.get('Authorization')).toBeNull();
  });
});
