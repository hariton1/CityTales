
package group_05.ase.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String supabaseId; // als String für Request, wird zu UUID geparst
}
