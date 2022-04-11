import { Component, OnInit } from '@angular/core';
import { User } from '../_models/user';
import { TokenStorageService } from '../_services/token-storage.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})

export class ProfileComponent implements OnInit {
  currentUser: User = ({} as any) as User;
  currentAccessToken: string ="";
  
  constructor(private token: TokenStorageService) {  }
  
  ngOnInit(): void {
    console.log(this.token.getUser())
    this.currentAccessToken = this.token.getToken()!;
    this.currentUser = this.token.getUser()!;
  }
}
