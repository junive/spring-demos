import { HTTP_INTERCEPTORS, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';
import { TokenStorageService } from '../_services/token-storage.service';
import { UserService } from '../_services/user.service';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';

const TOKEN_HEADER_KEY = 'Authorization';  // for Spring Boot back-end
//const TOKEN_HEADER_KEY = 'x-access-token';    // for Node.js Express back-end

@Injectable()

export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(
    private tokenStorageService: TokenStorageService, 
    private userService: UserService) { }

  intercept(
      req: HttpRequest<any>, 
      next: HttpHandler)
      : Observable<HttpEvent<Object>> {
    
    const token = this.tokenStorageService.getToken();

    if (token != null && !this.isRefreshing) {
      req = this.addTokenHeader(req, token);

      return next.handle(req).pipe(catchError(
        (err) => {
          if (err instanceof HttpErrorResponse 
            && err.error.custom_status == "TOKEN_EXPIRED") {
            return this.handleTokenRefreshError(req, next);
          }
          return throwError(() => new Error(err));
      }));
    } else  return next.handle(req);
  }

  private handleTokenRefreshError(request: HttpRequest<any>, next: HttpHandler) {
   
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);
      const refreshToken = this.tokenStorageService.getRefreshToken();
      
      if (refreshToken) {
        return this.userService.refreshToken(refreshToken).pipe(
          switchMap((response: Map<string, Object>) => {
         
            const res =  new Map(Object.entries(response));
            this.isRefreshing = false;
            this.tokenStorageService.saveToken(res.get("access_token"));
            this.refreshTokenSubject.next(res.get("access_token"));
            return next.handle(this.addTokenHeader(request, res.get("access_token")));
          }),
          catchError((err) => {
            this.isRefreshing = false;
            this.tokenStorageService.signOut();
            window.location.reload();
            return throwError(() => new Error(err));
          })
        );
      }
    }

    return this.refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap((token) => next.handle(this.addTokenHeader(request, token)))
    );
  }

  private addTokenHeader(request: HttpRequest<any>, token: string) {
    return request.clone({ 
      headers: request.headers.set(TOKEN_HEADER_KEY, 'Bearer '+token) 
    });
  }
}

export const authInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
];
