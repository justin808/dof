package dof_xml_handler;

import org.doframework.ScratchPkProvider;

/**
 *
 *
 */
public class DefaultScratchPkProvider implements ScratchPkProvider
{
    /**
     * currentTimeMillis too long for int
     */
    public String getScratchPk()
    {
        String big = System.currentTimeMillis() + "";
        String rightDigits = big.substring(big.length() - 8);
        return rightDigits;
    }

}
