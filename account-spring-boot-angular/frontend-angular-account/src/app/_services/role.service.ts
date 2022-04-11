import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

const API_URL = environment.apiAccountUrl;

@Injectable({
  providedIn: 'root'
})

export class RoleService {

  constructor(private http: HttpClient) { }

  public getPublicContent(): Observable<any> {
    return this.http.get(API_URL + '/content/all', { responseType: 'text' });
  }

  public getUserBoard(): Observable<any> {
    return this.http.get(API_URL + '/content/user', { responseType: 'text' });
  }


  public getAdminBoard(): Observable<any[]> {
    return this.http.get<any[]>(API_URL + '/user/all');
  }
}
