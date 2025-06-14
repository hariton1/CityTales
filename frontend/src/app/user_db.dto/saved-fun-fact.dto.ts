import { UUID } from "node:crypto";

export class SavedFunFactDto {
  private saved_fun_fact_id: number;
  private user_id: UUID;
  private article_id: number;
  private headline: string;
  private fun_fact: string;
  private image_url: string;
  private score: number;
  private reason: string;
  private saved_at: Date;

  constructor(saved_fun_fact_id: number, user_id: UUID, article_id: number, headline: string, fun_fact: string,
              image_url: string, score: number, reason: string, saved_at: Date) {
    this.saved_fun_fact_id = saved_fun_fact_id;
    this.user_id = user_id;
    this.article_id = article_id;
    this.headline = headline;
    this.fun_fact = fun_fact;
    this.image_url = image_url;
    this.score = score;
    this.reason = reason;
    this.saved_at = saved_at;
  }

  public getSavedFunFactId():number{
    return this.saved_fun_fact_id;
  }

  public setSavedFunFactId(fun_fact_id:number){
    this.saved_fun_fact_id = fun_fact_id;
  }

  public getUserId():UUID {
    return this.user_id;
  }

  public setUserId(user_id:UUID){
    this.user_id = user_id;
  }

  public getArticleId():number {
    return this.article_id;
  }

  public setArticleId(article_id:number){
    this.article_id = article_id;
  }

  public getHeadline():string {
    return this.headline;
  }

  public setHeadline(headline:string){
    this.headline = headline;
  }

  public getFunFact():string {
    return this.fun_fact;
  }

  public setFunFact(fun_fact:string){
    this.fun_fact = fun_fact;
  }

  public getImageUrl():string {
    return this.image_url;
  }

  public setImageUrl(image_url:string){
    this.image_url = image_url;
  }

  public getScore():number {
    return this.score;
  }

  public setScore(score:number){
    this.score = score;
  }

  public getReason():string {
    return this.reason;
  }

  public setReason(reason:string){
    this.reason = reason;
  }

  public getSavedAt():Date {
    return this.saved_at;
  }

  public setSavedAt(saved_at:Date){
    this.saved_at = saved_at;
  }

}
