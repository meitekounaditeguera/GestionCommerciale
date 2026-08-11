import { Component, EventEmitter, OnDestroy, AfterViewInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Html5Qrcode } from 'html5-qrcode';

// Modale de scan caméra (code-barres / QR code) : émet la valeur détectée puis se ferme
// automatiquement. Suit le même pattern de "modale" que le reste de l'application
// (backdrop + panel pilotés par un booléen dans le composant parent, pas de JS Bootstrap).
@Component({
  selector: 'app-scanner-code-barres',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './scanner-code-barres.html',
  styleUrls: ['./scanner-code-barres.css']
})
export class ScannerCodeBarresComponent implements AfterViewInit, OnDestroy {

  // Valeur détectée (référence produit, id, etc.).
  @Output() codeScanne = new EventEmitter<string>();
  // Demande de fermeture de la modale, avec ou sans détection.
  @Output() fermer = new EventEmitter<void>();

  // Message affiché si la caméra est refusée ou indisponible.
  erreur = '';

  private readonly lecteurId = 'lecteur-code-barres';
  private html5Qrcode: Html5Qrcode | null = null;
  private dejaDetecte = false;

  ngAfterViewInit(): void {
    this.demarrerScanner();
  }

  private demarrerScanner(): void {

    this.html5Qrcode = new Html5Qrcode(this.lecteurId);

    this.html5Qrcode
      .start(
        { facingMode: 'environment' },
        { fps: 10, qrbox: { width: 250, height: 250 } },
        (texteDecode) => this.onCodeDetecte(texteDecode),
        () => {
          // Callback appelé à chaque frame sans détection : rien à faire.
        }
      )
      .catch((err) => {
        console.error("Erreur lors de l'accès à la caméra :", err);
        this.erreur = "Caméra non accessible. Vérifiez que l'accès à la caméra est autorisé et qu'un appareil photo est disponible.";
      });

  }

  private onCodeDetecte(code: string): void {

    // La caméra continue d'émettre des détections tant qu'elle tourne : on ignore tout
    // ce qui suit la première détection valide, le temps que l'arrêt prenne effet.
    if (this.dejaDetecte) {
      return;
    }
    this.dejaDetecte = true;

    this.emettreBip();
    if (typeof navigator !== 'undefined' && navigator.vibrate) {
      navigator.vibrate(200);
    }

    this.arreterScanner().finally(() => {
      this.codeScanne.emit(code);
      this.fermer.emit();
    });

  }

  // Bip de confirmation généré via Web Audio API : aucun fichier audio à charger.
  private emettreBip(): void {
    try {
      const contexte = new AudioContext();
      const oscillateur = contexte.createOscillator();
      const gain = contexte.createGain();

      oscillateur.type = 'sine';
      oscillateur.frequency.value = 880;
      gain.gain.value = 0.2;

      oscillateur.connect(gain);
      gain.connect(contexte.destination);

      oscillateur.start();
      oscillateur.stop(contexte.currentTime + 0.15);
      oscillateur.onended = () => contexte.close();
    } catch {
      // Le bip est un confort, pas une exigence fonctionnelle : on ignore les échecs
      // (ex: navigateur sans Web Audio API).
    }
  }

  private arreterScanner(): Promise<void> {

    if (!this.html5Qrcode) {
      return Promise.resolve();
    }

    return this.html5Qrcode
      .stop()
      .then(() => this.html5Qrcode?.clear())
      .catch(() => {
        // Si le scanner n'a jamais démarré (permission refusée), stop() rejette : sans
        // conséquence puisqu'il n'y a alors aucun flux vidéo à arrêter.
      });

  }

  fermerManuel(): void {
    this.arreterScanner().finally(() => this.fermer.emit());
  }

  ngOnDestroy(): void {
    this.arreterScanner();
  }

}
