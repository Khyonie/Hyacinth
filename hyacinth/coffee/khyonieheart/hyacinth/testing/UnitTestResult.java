package coffee.khyonieheart.hyacinth.testing;

import coffee.khyonieheart.hyacinth.util.marker.NotNull;
import coffee.khyonieheart.hyacinth.util.marker.Nullable;

public record UnitTestResult(
    boolean pass, 
    @Nullable String description,
    @Nullable String failureReason,
    @NotNull UnitTestable testable
) {}
