package pub.tbc.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by tbc on 2018/4/18.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pair<F, S> {
    private F first;
    private S second;

    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }
}
