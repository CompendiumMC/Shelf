package io.github.compendiummc.shelf.cli;

import java.util.List;

import net.jbock.Parameter;
import net.jbock.SuperCommand;
import net.jbock.VarargsParameter;

@SuperCommand(name = "shelf")
interface ShelfCommand {

    @Parameter(index = 0)
    String command();

    @VarargsParameter
    List<String> rest();

}
