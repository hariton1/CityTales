import {UUID} from 'node:crypto';

export class UserDto {
  private _id: UUID;
  private _email: string;
  private _created_at: Date;
  private _role?: string;

  constructor(id: UUID, email: string, created_at: Date, role?: string) {
    this._id = id;
    this._email = email;
    this._created_at = created_at;
    this._role = role;
  }

  get id(): UUID {
    return this._id;
  }

  set id(value: UUID) {
    this._id = value;
  }

  get email(): string {
    return this._email;
  }

  set email(value: string) {
    this._email = value;
  }

  get created_at(): Date {
    return this._created_at;
  }

  set created_at(value: Date) {
    this._created_at = value;
  }

  get role(): string | undefined {
    return this._role;
  }

  set role(value: string | undefined) {
    this._role = value;
  }

  toString(): string {
    return (
      "User - " +
      this._id +
      " " +
      this._email +
      " " +
      this._created_at +
      " " +
      (this._role ? this._role : "")
    );
  }
}
