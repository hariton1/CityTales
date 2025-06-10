export interface Price {
    readonly priceId: number | null | undefined;
    locationId: number | null;
    price: number | null | undefined;
    name: string | null | undefined;
    description: string | null | undefined;
    readonly created_at: Date;
}
