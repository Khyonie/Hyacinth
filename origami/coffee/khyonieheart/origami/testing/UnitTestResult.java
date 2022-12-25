package coffee.khyonieheart.origami.testing;

import coffee.khyonieheart.origami.util.marker.NotNull;
import coffee.khyonieheart.origami.util.marker.Nullable;

public record UnitTestResult(
    boolean pass, 
    @Nullable String description,
    @Nullable String failureReason,
    @NotNull UnitTestable testable
) {}
