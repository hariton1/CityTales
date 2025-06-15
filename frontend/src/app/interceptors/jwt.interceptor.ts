import { HttpInterceptorFn } from '@angular/common/http';
import { HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { Observable, from } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { supabase } from '../user-management/supabase.service';


export const JwtInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {

  const skipJwtFor = [
    'http://localhost:7474/db/data'
  ];

  if (skipJwtFor.some(url => req.url.startsWith(url))) {
    return next(req);
  }

  return from(supabase.auth.getSession()).pipe(
    mergeMap(sessionResult => {
      const token = sessionResult.data.session?.access_token;
      if (token && !req.headers.has('Authorization')) {
        req = req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`
          }
        });
      }
      return next(req);
    })
  );
};
