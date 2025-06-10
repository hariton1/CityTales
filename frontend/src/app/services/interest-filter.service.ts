import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class InterestFilterService {
  private filteringSubject = new BehaviorSubject<boolean>(false);

  // 1. Observable für async binding oder subscription:
  getFiltering$(): Observable<boolean> {
    return this.filteringSubject.asObservable();
  }

  // 2. Sync-Getter (optional):
  get currentValue(): boolean {
    return this.filteringSubject.value;
  }

  // 3. Setter:
  setFiltering(value: boolean) {
    this.filteringSubject.next(value);
  }
}
