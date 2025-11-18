import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * Interceptor de desenvolvimento que injeta `X-User-Id` nas requisições.
 * Lê `userId` de `localStorage` (chave `userId`) ou usa um UUID dev fixo.
 */
@Injectable()
export class UserIdInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const stored = localStorage.getItem('userId');
    const userId = stored && stored.length > 0 ? stored : '00000000-0000-0000-0000-000000000001';
    const cloned = req.clone({ setHeaders: { 'X-User-Id': userId } });
    return next.handle(cloned);
  }
}
