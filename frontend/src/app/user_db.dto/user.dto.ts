import {UUID} from 'node:crypto';

export class UserDto {
  private _id: UUID;
  private _supabase_id: UUID;
  private _email: string;
  private _created_at: Date;
  private _display_name: string;
  private _is_active: boolean;

  constructor(id: UUID, supabase_id: UUID, email: string, created_at: Date, display_name: string, is_active: boolean) {
    this._id = id;
    this._supabase_id = supabase_id;
    this._email = email;
    this._created_at = created_at;
    this._display_name = display_name;
    this._is_active = is_active;
  }

  get id(): UUID {
    return this._id;
  }

  set id(value: UUID) {
    this._id = value;
  }

  get supabase_id(): UUID {
    return this._supabase_id;
  }

  set supabase_id(value: UUID) {
    this._supabase_id = value;
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

  get display_name(): string {
    return this._display_name;
  }

  set display_name(value: string) {
    this._display_name = value;
  }

  get is_active(): boolean {
    return this._is_active;
  }

  set is_active(value: boolean) {
    this._is_active = value;
  }

  toString(): string {
    return "User - " + this._id + ' ' + this._supabase_id + ' ' + this._email + ' ' + this._created_at + ' ' + this._display_name + ' ' + this._is_active;
  }
}
