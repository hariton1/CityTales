import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class InterestFilterService {
  private filteringSubject = new BehaviorSubject<boolean>(false);
  filtering$ = this.filteringSubject.asObservable();

  setFiltering(value: boolean) {
    this.filteringSubject.next(value);
  }

  getFiltering(): boolean {
    return this.filteringSubject.value;
  }
}
