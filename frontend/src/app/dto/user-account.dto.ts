export class UserAccountDto {
  private readonly userId: number;
  private username: string;
  private email: string;
  private readonly passwordHash: string;
  private xp: number;
  private readonly badges: string[];

  constructor(username: string, email: string, passwordHash: string) {
    this.userId = -1;
    this.username = username;
    this.email = email;
    this.passwordHash = passwordHash;
    this.xp = 0;
    this.badges = [];
  }

  setUsername(username: string): void {
    this.username = username;
  }

  setEmail(email: string): void {
    this.email = email;
  }

  setXp(xp: number): void {
    this.xp = xp;
  }

  addBadge(badge: string): void {
    this.badges.push(badge);
  }

  removeBadge(badge: string): void {
    this.badges.splice(this.badges.indexOf(badge), 1);
  }

  getUserID(): number {
    return this.userId;
  }

  getUsername(): string {
    return this.username;
  }

  getEmail(): string {
    return this.email;
  }

  getPasswordHash(): string {
    return this.passwordHash;
  }

  getXp(): number {
    return this.xp;
  }

  getBadges(): string[] {
    return this.badges;
  }
}
