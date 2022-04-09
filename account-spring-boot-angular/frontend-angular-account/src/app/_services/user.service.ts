import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { User } from '../_models/user';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

const AUTH_API = environment.apiAccountUrl;

const httpHeaderJson = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

const httpHeaderEncoded = {
  headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' })
};

@Injectable({
  providedIn: 'root'
})

export class UserService {

  constructor(private http: HttpClient) { }

  public login(user: User): Observable<Map<string, Object>> {
    const sendBody = new HttpParams()
      .set('username', user.username)
      .set('password', user.password);

    return this.http.post<Map<string, Object>>(
      AUTH_API + '/login', 
      sendBody, 
      httpHeaderEncoded);
  }

  public getUserByName(username: string): Observable<User> {
    return this.http.get<User>(
      AUTH_API + `/user/find/${username}`, 
      httpHeaderJson);
  }

  public register(user: User): Observable<User> {
    return this.http.post<User>(
      AUTH_API + '/user/save', 
      user,
      httpHeaderJson);
  }
}
