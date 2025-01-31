package org.cyclops.commoncapabilities.api.capability.recipehandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.cyclops.commoncapabilities.IngredientComponents;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ItemMatch;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.MixedIngredients;
import org.cyclops.commoncapabilities.api.ingredient.PrototypedIngredient;
import org.cyclops.commoncapabilities.ingredient.DataComparator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class TestRecipeDefinition {

    private static RecipeDefinition D_1;
    private static RecipeDefinition D_1_bis;
    private static RecipeDefinition D_2;
    private static RecipeDefinition D_3;

    private static void setStatic(Field field, Object newValue) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(null, newValue);
    }

    @BeforeClass
    public static void init() throws NoSuchFieldException, IllegalAccessException {
        SharedConstants.setVersion(DetectedVersion.BUILT_IN);
        Bootstrap.bootStrap();
        setStatic(IngredientComponent.class.getField("ITEMSTACK"), IngredientComponents.ITEMSTACK);

        Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> dInputMap = Maps.newIdentityHashMap();
        dInputMap.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                new PrototypedIngredientAlternativesList<>(Lists.newArrayList(
                        new PrototypedIngredient<>(IngredientComponents.ITEMSTACK, new ItemStack(Items.APPLE), ItemMatch.EXACT)
                ))
        ));
        Map<IngredientComponent<?, ?>, List<?>> dOutputMap = Maps.newIdentityHashMap();
        dOutputMap.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                new ItemStack(Items.SUGAR)
        ));
        D_1 = new RecipeDefinition(dInputMap, new MixedIngredients(dOutputMap));

        Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> dInputMap2 = Maps.newIdentityHashMap();
        dInputMap2.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                new PrototypedIngredientAlternativesList<>(Lists.newArrayList(
                        new PrototypedIngredient<>(IngredientComponents.ITEMSTACK, new ItemStack(Items.DIAMOND, 2), ItemMatch.EXACT)
                ))
        ));
        Map<IngredientComponent<?, ?>, List<?>> dOutputMap2 = Maps.newIdentityHashMap();
        dOutputMap2.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                new ItemStack(Items.SUGAR)
        ));
        D_2 = new RecipeDefinition(dInputMap2, new MixedIngredients(dOutputMap2));

        Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> dInputMap3 = Maps.newIdentityHashMap();
        dInputMap3.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                new PrototypedIngredientAlternativesItemStackTag(Lists.newArrayList("logs"), ItemMatch.EXACT, 1)
        ));
        Map<IngredientComponent<?, ?>, List<?>> dOutputMap3 = Maps.newIdentityHashMap();
        dOutputMap3.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                new ItemStack(Items.SUGAR)
        ));
        D_3 = new RecipeDefinition(dInputMap3, new MixedIngredients(dOutputMap3));

        Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> dInputMapBis = Maps.newIdentityHashMap();
        dInputMapBis.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                new PrototypedIngredientAlternativesList<>(Lists.newArrayList(
                        new PrototypedIngredient<>(IngredientComponents.ITEMSTACK, new ItemStack(Items.APPLE), ItemMatch.EXACT)
                ))
        ));
        Map<IngredientComponent<?, ?>, List<?>> dOutputMapBis = Maps.newIdentityHashMap();
        dOutputMapBis.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                new ItemStack(Items.SUGAR)
        ));
        D_1_bis = new RecipeDefinition(dInputMapBis, new MixedIngredients(dOutputMapBis));

        ItemMatch.DATA_COMPARATOR = DataComparator.INSTANCE = new DataComparator(null);
    }

    @Test
    public void testGetInputComponents() {
        assertThat(D_1.getInputComponents(), equalTo(Sets.newHashSet(IngredientComponents.ITEMSTACK)));
    }

    @Test
    public void testGetInput() {
        assertThat(D_1.getInputs(IngredientComponents.ITEMSTACK), equalTo(Lists.newArrayList(
                new PrototypedIngredientAlternativesList<>(Lists.newArrayList(
                        new PrototypedIngredient<>(IngredientComponents.ITEMSTACK, new ItemStack(Items.APPLE), ItemMatch.EXACT)
                ))
        )));
        assertThat(D_1.getInputs(IngredientComponents.FLUIDSTACK), equalTo(Lists.newArrayList()));
    }

    @Test
    public void testGetOutput() {
        Map<IngredientComponent<?, ?>, List<?>> dOutputMap2 = Maps.newIdentityHashMap();
        dOutputMap2.put(IngredientComponents.ITEMSTACK, Lists.newArrayList(
                new ItemStack(Items.SUGAR)
        ));
        assertThat(D_1.getOutput(), equalTo(new MixedIngredients(dOutputMap2)));
    }

    @Test
    public void testEquals() {
        assertThat(D_1, equalTo(D_1_bis));
        assertThat(D_1_bis, equalTo(D_1));

        assertThat(D_1, not(equalTo(D_2)));
        assertThat(D_2, not(equalTo(D_1)));

        assertThat(D_1, not(equalTo(D_3)));
        assertThat(D_3, not(equalTo(D_1)));
    }

    @Test
    public void testHashCode() {
        assertThat(D_1.hashCode(), equalTo(D_1_bis.hashCode()));
        assertThat(D_1_bis.hashCode(), equalTo(D_1.hashCode()));
    }

    @Test
    public void testCompareTo() {
        assertThat(D_1.compareTo(D_1_bis), is(0));
        assertThat(D_1_bis.compareTo(D_1), is(0));

        assertThat(D_1.compareTo(D_2), is(-5));
        assertThat(D_2.compareTo(D_1), is(5));

        assertThat(D_1.compareTo(D_3), is(1));
        assertThat(D_3.compareTo(D_1), is(-1));
    }

}
