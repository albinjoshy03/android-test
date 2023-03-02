"""Combines multiple jars into one jar."""

def combine_jars(ctx, input_jars, output):
    if not input_jars:
        fail("must provide at least one input_jar")
    if not output:
        fail("must provide output file")

    command = "{combine_jars_bin} {outfile} {input_jars} ".format(
        combine_jars_bin = ctx.executable._combine_jars_java.path,
        outfile = output.path,
        input_jars = " ".join([jar.path for jar in input_jars]),
    )

    ctx.actions.run_shell(
        command = command,
        inputs = input_jars,
        outputs = [output],
        tools = [ctx.executable._combine_jars_java],
    )
