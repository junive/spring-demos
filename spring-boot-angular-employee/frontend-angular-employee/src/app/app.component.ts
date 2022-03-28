import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Employee } from './employee';
import { EmployeeService } from './employee.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  public employees: Employee[] = [];
  public editEmployee?: Employee;
  public deleteEmployee?: Employee;

  constructor(private employeeService: EmployeeService) {}

  ngOnInit() {
    this.getEmployees();
  }

  public getEmployees(): void {
    this.employeeService.getEmployees().subscribe({
      next : (res: Employee[]) => { this.employees = res; },
      error: (err: HttpErrorResponse) => {  alert(err.message); }
    })
  }

  public onAddEmployee(addForm: NgForm) {
    document.getElementById("add_employee_form")?.click();
    this.employeeService.addEmployee(addForm.value).subscribe( {
        next : (res: Employee) => { 
          //console.log(res);
          this.getEmployees(); // reload all employees
          addForm.reset();
         },
        error: (err: HttpErrorResponse) => {  alert(err.message); }
      })
  }

  public onUpdateEmployee(employee: Employee) {
    document.getElementById("update_employee_form")?.click();
    this.employeeService.updateEmployee(employee).subscribe( {
        next : (res: Employee) => { 
          this.getEmployees(); // reload all employees
         },
        error: (err: HttpErrorResponse) => {  alert(err.message); }
      })
  }

  public onDeleteEmloyee(id?: number) {
    document.getElementById("delete_employee_form")?.click();
    this.employeeService.deleteEmployee(id!).subscribe( {
      next : (res: void) => { 
        this.getEmployees(); // reload all employees
       },
        error: (err: HttpErrorResponse) => {  alert(err.message); }
      })
  }
  
  public searchEmployees(key: string): void {
    const results: Employee[] = [];
    for (const employee of this.employees) {
      if (employee.name.toLowerCase().indexOf(key.toLowerCase()) !== -1
      || employee.email.toLowerCase().indexOf(key.toLowerCase()) !== -1
      || employee.phone.toLowerCase().indexOf(key.toLowerCase()) !== -1
      || employee.jobTitle.toLowerCase().indexOf(key.toLowerCase()) !== -1) {
        results.push(employee);
      }
    }
    this.employees = results;
    if (results.length === 0 || !key) {
      this.getEmployees();
    }
  }
  
  public onOpenModal(mode: string, employee?: Employee): void {
    const container = document.getElementById("main_container");
    const button = document.createElement("button");
    button.type = "button";
    button.style.display = "none";
    button.setAttribute("data-toggle", "modal");
    if (mode === "add") {
      button.setAttribute("data-target", "#addEmployeeModal");
    } else if (mode === "edit") {
      this.editEmployee = employee;
      button.setAttribute("data-target", "#updateEmployeeModal");
    } else if (mode === "delete") {
      this.deleteEmployee = employee;
      button.setAttribute("data-target", "#deleteEmployeeModal");
    }
    container?.appendChild(button);
    button.click();
  }

}
