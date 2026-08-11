import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { RoleGuard } from './role.guard';
import { AuthService } from '../services/auth.service';

describe('RoleGuard', () => {
  let guard: RoleGuard;
  let authServiceMock: { hasAnyRole: ReturnType<typeof vi.fn> };
  let routerMock: { navigate: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    authServiceMock = { hasAnyRole: vi.fn() };
    routerMock = { navigate: vi.fn() };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    });

    guard = TestBed.inject(RoleGuard);
  });

  // Une ActivatedRouteSnapshot réelle porte beaucoup de propriétés inutiles ici : un simple
  // objet avec la forme attendue (route.data.roles) suffit pour ce test de garde.
  function routeAvecRoles(roles?: string[]): ActivatedRouteSnapshot {
    return { data: { roles } } as unknown as ActivatedRouteSnapshot;
  }

  it("autorise l'accès si l'utilisateur possède un des rôles autorisés par la route", () => {
    authServiceMock.hasAnyRole.mockReturnValue(true);

    const resultat = guard.canActivate(routeAvecRoles(['ROLE_ADMIN', 'ROLE_GESTIONNAIRE']));

    expect(resultat).toBe(true);
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it("bloque l'accès et redirige vers l'accueil si le rôle de l'utilisateur est insuffisant", () => {
    authServiceMock.hasAnyRole.mockReturnValue(false);

    const resultat = guard.canActivate(routeAvecRoles(['ROLE_ADMIN']));

    expect(resultat).toBe(false);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/']);
  });

  it("autorise l'accès sans vérifier le rôle si la route ne définit aucune restriction", () => {
    const resultat = guard.canActivate(routeAvecRoles([]));

    expect(resultat).toBe(true);
    expect(authServiceMock.hasAnyRole).not.toHaveBeenCalled();
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });
});
