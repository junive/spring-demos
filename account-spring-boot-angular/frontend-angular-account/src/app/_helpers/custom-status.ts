export class CustomStatus {
    private error: string;
    private message:Map<string, string>;
    private customStatus: string;
    private badValue: string;

    constructor(private errorMessage:string) {
        this.error = errorMessage;
        this.message = new Map(Object.entries(errorMessage));
        this.customStatus = this.message.get("custom_status")!;
        this.badValue = this.message.get("bad_value")!;
    }


    public  getRegisterError(): string {
        if (this.customStatus == "USERNAME_EXIST") {
            return "Username "+this.badValue+" already exists !";
        } else if (this.customStatus == "EMAIL_EXIST") {
            return "The email "+this.badValue+" already exists !";
        }
        return this.error;
    }

    public  getLoginError(): string {
        if (this.customStatus == "USERNAME_PASSWORD_NOT_FOUND") {
            return "Username or Password doesn't exist !";
        } 
        return this.error;
    }
}
