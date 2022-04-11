import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { User } from '../_models/user';
import { UserService } from '../_services/user.service';
import { CustomStatus } from '../_helpers/custom-status';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})

export class RegisterComponent implements OnInit {

  registerForm: any = {
    username: null,
    email: null,
    password: null,
    roles: []
  };


  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';

  constructor(private authService: UserService) { }
 
  ngOnInit(): void { }

  public onRegisterSubmit(): void {
    this.registerForm.name = this.registerForm.username;
    this.authService.register(this.registerForm).subscribe({
      next: (data: User) => {
        this.isSuccessful = true;
        this.isSignUpFailed = false;
      },
      error: (err: HttpErrorResponse) => {
       // const error: CustomException = err.error as CustomException;
       
        const status: CustomStatus = new CustomStatus(err.error);
        this.errorMessage = status.getRegisterError();
       
        this.isSignUpFailed = true;
      }
    });
    
  }

}
