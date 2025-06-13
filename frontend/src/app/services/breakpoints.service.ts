import { Injectable } from '@angular/core';
import { BreakpointObserver, BreakpointState } from '@angular/cdk/layout';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

type CustomBreakpointLevel =
  | 'mobile'
  | 'tablet'
  | 'desktop'
  | '1280-1499'
  | '1500-1899'
  | '1900-2559'
  | '2560+'
  | null;

@Injectable({
  providedIn: 'root',
})

export class BreakpointService {
  private readonly BREAKPOINTS = {
    mobile: '(min-width: 360px) and (max-width: 767px)',
    tablet: '(min-width: 768px) and (max-width: 1023px)',
    desktop: '(min-width: 1024px) and (max-width: 1279px)',
    b1280: '(min-width: 1280px) and (max-width: 1499px)',
    b1500: '(min-width: 1500px) and (max-width: 1899px)',
    b1900: '(min-width: 1900px) and (max-width: 2559px)',
    b2560: '(min-width: 2560px)',
  };

  private levelSubject = new BehaviorSubject<CustomBreakpointLevel>(null);
  readonly level$: Observable<CustomBreakpointLevel> = this.levelSubject.asObservable();

  constructor(readonly observer: BreakpointObserver) {
    this.observer
      .observe(Object.values(this.BREAKPOINTS))
      .pipe(
        map((result: BreakpointState): CustomBreakpointLevel => {
          const b = result.breakpoints;

          if (b[this.BREAKPOINTS.mobile]) return 'mobile';
          if (b[this.BREAKPOINTS.tablet]) return 'tablet';
          if (b[this.BREAKPOINTS.desktop]) return 'desktop';
          if (b[this.BREAKPOINTS.b1280]) return '1280-1499';
          if (b[this.BREAKPOINTS.b1500]) return '1500-1899';
          if (b[this.BREAKPOINTS.b1900]) return '1900-2559';
          if (b[this.BREAKPOINTS.b2560]) return '2560+';
          return null;
        })
      )
      .subscribe(this.levelSubject);
  }

  get currentLevel(): CustomBreakpointLevel {
    return this.levelSubject.value;
  }

  get isMobile(): boolean {
    return ['mobile', 'tablet'].includes(this.currentLevel!);
  }

  /** Smart items count based on screen */
  get tonesItemCount(): number {
    switch (this.currentLevel) {
      case 'mobile':
      case 'tablet':
        return 2;
      case 'desktop':
        return 2;
      case '1280-1499':
        return 3;
      case '1500-1899':
        return 3;
      case '1900-2559':
        return 4;
      case '2560+':
        return 4;
      default:
        return 3;
    }
  }

  get relatedItemCount(): number {
    switch (this.currentLevel) {
      case 'mobile':
      case 'tablet':
        return 2;
      case 'desktop':
        return 2;
      case '1280-1499':
        return 2;
      case '1500-1899':
        return 2;
      case '1900-2559':
        return 3;
      case '2560+':
        return 3;
      default:
        return 3;
    }
  }

}
