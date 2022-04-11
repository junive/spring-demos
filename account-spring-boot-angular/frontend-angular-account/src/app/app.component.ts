import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { Role } from './_models/role';
import { TokenStorageService } from './_services/token-storage.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit {
  private roles: Role[] = [];
  isLoggedIn = false;
  showAdminBoard = false;
  showModeratorBoard = false;
  username?: string;
  eventBusSub?: Subscription;

  constructor(
    private tokenStorageService: TokenStorageService) { }

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getToken();
    if (this.isLoggedIn) {
      const user = this.tokenStorageService.getUser()!;
      this.roles = user.roles;

      const roleNames = this.roles.map(role => role.name);
      this.showAdminBoard = roleNames.includes('ROLE_ADMIN');
      this.username = user.username;
    }
    /*this.eventBusSub = this.eventBusService.on('logout', () => {
      this.logout();
    });*/
  }

  ngOnDestroy(): void {
   // if (this.eventBusSub)
      //this.eventBusSub.unsubscribe();
  }

  public logout(): void {
    this.tokenStorageService.signOut();
    window.location.reload();
  }
}
