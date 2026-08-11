import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataRefreshService {
  private readonly dataChangedSubject = new Subject<void>();

  dataChanged$ = this.dataChangedSubject.asObservable();

  notifyDataChanged(): void {
    this.dataChangedSubject.next();
  }
}
