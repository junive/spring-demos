import { Component, OnInit } from '@angular/core';
import { UserService } from '../_services/user.service';
import { TokenStorageService } from '../_services/token-storage.service';
import { Role } from '../_models/role';
import { User } from '../_models/user';
import { HttpErrorResponse } from '@angular/common/http';
import { CustomStatus } from '../_helpers/custom-status';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {

  loginForm: any = {
    username: null,
    password: null
  };

  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: Role[] = [];

  constructor(private userService: UserService, private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
      this.roles = this.tokenStorage.getUser()!.roles;
    }
  }


  public onLoginSubmit(): void {
    // Create a new token first and store it
    this.userService.login(this.loginForm).subscribe({
      next: (data: Map<string, Object>) => {
        const tokens = new Map(Object.entries(data));
        const user: User = tokens.get("user_response") as User;

        this.tokenStorage.saveToken(tokens.get("access_token") );
        this.tokenStorage.saveRefreshToken(tokens.get("refresh_token") );
        this.tokenStorage.saveUser(user);
        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.roles = this.tokenStorage.getUser()!.roles;
        this.reloadPage();
      },
      error: (err: HttpErrorResponse) => {
        const status: CustomStatus = new CustomStatus(err.error);
        this.errorMessage = status.getLoginError();
        this.isLoginFailed = true;
      }

    });


  }

  public reloadPage(): void {
    window.location.reload();
  }
}
