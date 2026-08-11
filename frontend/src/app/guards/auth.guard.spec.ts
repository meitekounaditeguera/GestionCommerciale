import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { AuthGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let authServiceMock: { isAuthenticated: ReturnType<typeof vi.fn> };
  let routerMock: { navigate: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    authServiceMock = { isAuthenticated: vi.fn() };
    routerMock = { navigate: vi.fn() };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    });

    guard = TestBed.inject(AuthGuard);
  });

  it("autorise l'accès si l'utilisateur est authentifié", () => {
    authServiceMock.isAuthenticated.mockReturnValue(true);

    expect(guard.canActivate()).toBe(true);
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it("redirige vers /login et bloque l'accès si l'utilisateur n'est pas authentifié", () => {
    authServiceMock.isAuthenticated.mockReturnValue(false);

    expect(guard.canActivate()).toBe(false);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });
});
