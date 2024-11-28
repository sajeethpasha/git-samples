import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SamGlassRankerTest {

    private SamGlassRanker samGlassRanker;
    private AdsConfig adsConfig;
    private SelectionInput selectionInput;
    private NodeSelectedContent nodeSelectedContent;

    @BeforeEach
    public void setUp() {
        samGlassRanker = new SamGlassRanker();
        adsConfig = Mockito.mock(AdsConfig.class);
        selectionInput = Mockito.mock(SelectionInput.class);
        nodeSelectedContent = Mockito.mock(NodeSelectedContent.class);
    }

    @Test
    public void testProcessContent_BoostingEnabled() {
        // Arrange
        when(adsConfig.getModel()).thenReturn("someModel");
        when(adsConfig.isBoostingEnabled("someModel")).thenReturn(true);
        
        // Act
        NodeSelectedContent result = samGlassRanker.processContent("zone1", nodeSelectedContent, adsConfig, selectionInput);
        
        // Assert
        assertEquals(nodeSelectedContent, result);
    }

    @Test
    public void testProcessContent_BoostingNotEnabled_EmptySelectedNodes() {
        // Arrange
        when(adsConfig.getModel()).thenReturn("someModel");
        when(adsConfig.isBoostingEnabled("someModel")).thenReturn(false);
        when(nodeSelectedContent.getSelectedNodes()).thenReturn(Collections.emptyList());
        
        // Act
        NodeSelectedContent result = samGlassRanker.processContent("zone1", nodeSelectedContent, adsConfig, selectionInput);
        
        // Assert
        assertEquals(nodeSelectedContent, result);
        verify(nodeSelectedContent, times(1)).getSelectedNodes();
    }

    @Test
    public void testProcessContent_BoostingNotEnabled_WithSelectedNodes() {
        // Arrange
        when(adsConfig.getModel()).thenReturn("someModel");
        when(adsConfig.isBoostingEnabled("someModel")).thenReturn(false);

        List<NodeFeatures> nodeFeatures = new ArrayList<>();
        NodeFeatures feature = Mockito.mock(NodeFeatures.class);
        nodeFeatures.add(feature);
        when(nodeSelectedContent.getSelectedNodes()).thenReturn(nodeFeatures);

        // Act
        NodeSelectedContent result = samGlassRanker.processContent("zone1", nodeSelectedContent, adsConfig, selectionInput);
        
        // Assert
        assertNotNull(result);
        assertEquals(nodeSelectedContent, result);
    }

    @Test
    public void testProcessContent_ExceptionHandling() {
        // Arrange
        when(adsConfig.getModel()).thenThrow(new RuntimeException("Mock Exception"));

        // Act & Assert
        assertDoesNotThrow(() -> samGlassRanker.processContent("zone1", nodeSelectedContent, adsConfig, selectionInput));
    }

    @Test
    public void testProcessContent_WithAdsReRank() {
        // Arrange
        when(adsConfig.getModel()).thenReturn("someModel");
        when(adsConfig.isBoostingEnabled("someModel")).thenReturn(false);
        when(nodeSelectedContent.getMaxItemList()).thenReturn(5);
        List<NodeFeatures> nodeFeatures = new ArrayList<>();
        NodeFeatures feature = Mockito.mock(NodeFeatures.class);
        nodeFeatures.add(feature);
        when(nodeSelectedContent.getSelectedNodes()).thenReturn(nodeFeatures);
        
        // Act
        NodeSelectedContent result = samGlassRanker.processContent("zone1", nodeSelectedContent, adsConfig, selectionInput);
        
        // Assert
        assertEquals(nodeSelectedContent, result);
        verify(nodeSelectedContent, times(1)).setSelectedNodes(anyList());
    }
}
