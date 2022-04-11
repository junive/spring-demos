import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { RoleService } from '../_services/role.service';

@Component({
  selector: 'app-board-admin',
  templateUrl: './board-admin.component.html',
  styleUrls: ['./board-admin.component.css']
})

export class BoardAdminComponent implements OnInit {
  usersString?: String[] = [];
  errorMessage: string = "";

  constructor(
    private roleService: RoleService) { }

  ngOnInit(): void {
    this.roleService.getAdminBoard().subscribe({
      next: (data: any[]) => {
        
        data.forEach(user => {
          this.usersString?.push(JSON.stringify(user));
        });
       
      },
      error: (err: HttpErrorResponse) => {
       
        if (err.status == 403) {
          //this.eventBusService.emit(new EventData('logout', null));
          this.errorMessage = "You do not have the permission to access this board";
        } else  this.errorMessage = JSON.stringify(err.error) || err.message;
       // console.log("BOARD : "+ JSON.stringify(err));
       
       
       
      }});

  }
}
