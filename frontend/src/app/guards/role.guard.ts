import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// Interdit l'accès à une route si l'utilisateur n'a aucun des rôles
// listés dans `data: { roles: [...] }` de la définition de route.
@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const rolesAutorises = (route.data?.['roles'] as string[] | undefined) ?? [];

    if (rolesAutorises.length === 0 || this.authService.hasAnyRole(rolesAutorises)) {
      return true;
    }

    this.router.navigate(['/']);
    return false;
  }
}
