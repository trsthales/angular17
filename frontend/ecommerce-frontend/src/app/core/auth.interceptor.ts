import { Injectable, inject } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenService } from './token.service';
import { UserIdService } from './user-id.service';

/**
 * Interceptor que adiciona os headers necessários em todas as requisições:
 * - `Authorization: Bearer <token>` quando presente
 * - `X-User-Id` quando disponível no `UserIdService`
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private tokenSvc = inject(TokenService);
  private userSvc = inject(UserIdService);

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.tokenSvc.getToken();
    const userId = this.userSvc.userId();
    const setHeaders: Record<string, string> = {};
    if (token) setHeaders['Authorization'] = `Bearer ${token}`;
    if (userId) setHeaders['X-User-Id'] = userId;
    const cloned = req.clone({ setHeaders });
    return next.handle(cloned);
  }
}
