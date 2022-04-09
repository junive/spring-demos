import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { User } from '../_models/user';
import { CustomException } from '../_models/custom-exception';
import { UserService } from '../_services/user.service';

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
        
        const exception: CustomException = err.error as CustomException
        console.log(exception);
        if (!exception || !exception.custom_status) {
          this.errorMessage = "Unknow error : "+ err.message + " with Status :" + err.statusText;
        } else if (exception.custom_status == "USERNAME_EXIST") {
          this.errorMessage = "Username '"+exception.wrong+"' already extist ! ";
        } else if (exception.custom_status == "EMAIL_EXIST") {
          this.errorMessage = "The email '"+exception.wrong+"' already extist ! ";
        }
        //const exception = new CustomException();
        this.isSignUpFailed = true;
      }
    });
    
  }

}
