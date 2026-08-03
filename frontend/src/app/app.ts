import { Component, signal } from '@angular/core';
import { ClientListComponent } from './components/client-list/client-list';
import { ProduitListComponent } from './components/produit-list/produit-list';
import { NavbarComponent } from './components/layout/navbar/navbar';
import { DashboardComponent } from './components/dashboard/dashboard';
import { CommandeListComponent } from './components/commande-list/commande-list';


@Component({
  selector: 'app-root',
  standalone: true,

  // Tous les composants/modules utilisés par App
  imports: [ 

    ClientListComponent, 
    ProduitListComponent,
    NavbarComponent,
    DashboardComponent,
    CommandeListComponent,

  ],

  templateUrl: './app.html',
  styleUrl: './app.css',
})

export class App {

  // Signal contenant le titre de l'application
  protected readonly title = signal('frontend');

}

