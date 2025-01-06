/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java2ddrawingapplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Negein Immen
 */
public class DrawingApplicationFrame extends JFrame
{

    // Create the panels for the top of the application. One panel for each
    private final JPanel firstLine, secondLine;
    
    // line and one to contain both of those panels.
    private final JPanel topPanel;

    // create the widgets for the firstLine Panel.
    private final JComboBox<String> shapeComboBox;
    private final JLabel shapeLabel;
    private final JButton firstColorButton, secondColorButton, undoButton, clearButton;
    private Color colorOne, colorTwo;
    private final String[] shapes = {"Rectangle", "Oval", "Line"};

    //create the widgets for the secondLine Panel.
    private final JCheckBox filledCheckBox, gradientCheckBox, dashedCheckBox;
    private final JLabel optionsLabel, lineWidthLabel, dashLengthLabel;
    private final JSpinner lineWidthJSpinner, dashLengthJSpinner;

    // Variables for drawPanel.
    private final JPanel drawJPanel;
    private final DrawPanel drawPanel;
    private ArrayList<MyShapes> shapesArrayList;

    // add status label
    private final JLabel statusLabel;
  
    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame()
    {
        super("Java 2D Drawings");
        
        // add widgets to panels
        
        // firstLine widgets
        firstLine = new JPanel();
        firstLine.setBackground(Color.CYAN);
        shapeLabel = new JLabel("Shape:");
        firstLine.add(shapeLabel);
        
        firstLine.add(shapeComboBox = new JComboBox<>(shapes));
        
        firstLine.add(firstColorButton = new JButton("1st Color..."));
        firstLine.add(secondColorButton = new JButton("2nd Color..."));
        firstLine.add(undoButton = new JButton("Undo"));
        firstLine.add(clearButton = new JButton("Clear"));

        // secondLine widgets
        secondLine = new JPanel();
        secondLine.setBackground(Color.CYAN);
        optionsLabel = new JLabel("Options:");
        secondLine.add(optionsLabel);
        
        secondLine.add(filledCheckBox = new JCheckBox("Filled"));
        secondLine.add(gradientCheckBox = new JCheckBox("Use Gradient"));
        secondLine.add(dashedCheckBox = new JCheckBox("Dashed"));
        
        lineWidthLabel = new JLabel("Line Width:");
        secondLine.add(lineWidthLabel);
        lineWidthJSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 99, 1));
        lineWidthJSpinner.setValue(4);
        secondLine.add(lineWidthJSpinner);
        
        dashLengthLabel = new JLabel("Dash Length:");
        secondLine.add(dashLengthLabel);
        dashLengthJSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 99, 1));
        dashLengthJSpinner.setValue(15);
        secondLine.add(dashLengthJSpinner);

        // add top panel of two panels
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2,1));
        topPanel.add(firstLine);
        topPanel.add(secondLine);

        // add topPanel to North, drawPanel to Center, and statusLabel to South
        add(topPanel, BorderLayout.NORTH);
        
        drawJPanel = new JPanel();
        drawPanel = new DrawPanel();
        drawJPanel.setLayout(new BorderLayout());
        drawJPanel.add(drawPanel, BorderLayout.CENTER);
        add(drawJPanel, BorderLayout.CENTER);
        
        statusLabel = new JLabel();
        statusLabel.setBackground(Color.GRAY);
        add(statusLabel, BorderLayout.SOUTH);
        
        //add listeners and event handlers
        ActionListener colorOneListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                colorOne = JColorChooser.showDialog(DrawingApplicationFrame.this, "Choose a color", colorOne);
                
                if (colorOne == null)
                    colorOne = Color.LIGHT_GRAY;
            }
        };
        
        ActionListener colorTwoListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                colorOne = JColorChooser.showDialog(DrawingApplicationFrame.this, "Choose a color", colorTwo);
                
                if (colorTwo == null)
                    colorTwo = Color.LIGHT_GRAY;
            }
        };
        
        ActionListener undoListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                if (!shapesArrayList.isEmpty())
                {
                    shapesArrayList.remove(shapesArrayList.size() - 1);
                    repaint();
                }
            }
        };
        
        ActionListener clearListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                shapesArrayList.clear();
                repaint();
            }
        };
        
        firstColorButton.addActionListener(colorOneListener);
        secondColorButton.addActionListener(colorTwoListener);
        undoButton.addActionListener(undoListener);
        clearButton.addActionListener(clearListener); 
    }

    // Create event handlers, if needed

    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel
    {        
        private String selectedShape;
        
        public DrawPanel()
        {
            shapesArrayList = new ArrayList<>();
            MouseHandler handler = new MouseHandler();
            drawJPanel.addMouseListener(handler);
            drawJPanel.addMouseMotionListener(handler);
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            this.setBackground(Color.WHITE);
            //loop through and draw each shape in the shapes arraylist
            for (MyShapes shape : shapesArrayList)
            {
                shape.draw(g2d);
            }
        }

        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {
            private Point startPoint, endPoint;

            public void mousePressed(MouseEvent event)
            {
                startPoint = event.getPoint();
                endPoint = event.getPoint();
                Paint paint;
                Stroke stroke;
                int lineWidth = (Integer) lineWidthJSpinner.getValue();
                float[] dashLength = {(float)(int) dashLengthJSpinner.getValue()};
                Boolean isFilled = filledCheckBox.isSelected();
                MyShapes shape;
                selectedShape = (String) shapeComboBox.getSelectedItem();
                
                if (gradientCheckBox.isSelected())
                {
                    paint = new GradientPaint(0, 0, colorOne, 50, 50, colorTwo, true);
                } else {
                    paint = colorOne;
                }
                
                if (dashedCheckBox.isSelected()) {
                    stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashLength, 0);
                } else {
                    stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                }
               
                switch (selectedShape)
                {
                    case "Rectangle":
                        shape = new MyRectangle(startPoint, endPoint, paint, stroke, isFilled);
                        break;
                    case "Oval":
                        shape = new MyOval(startPoint, endPoint, paint, stroke, isFilled);
                        break;
                    case "Line":
                        shape = new MyLine(startPoint, endPoint, paint, stroke);
                        break;
                    default:
                        return;
                }
                 shapesArrayList.add(shape);
                 repaint();
            }

            public void mouseReleased(MouseEvent event)
            {
                
            }

            @Override
            public void mouseDragged(MouseEvent event)
            {
                endPoint = event.getPoint();
                shapesArrayList.get(shapesArrayList.size() - 1).setEndPoint(endPoint);
                repaint();
                statusLabel.setText(String.format("[%d, %d]", event.getX(), event.getY()));
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                statusLabel.setText(String.format("[%d, %d]", event.getX(), event.getY()));
            }
        }

    }
}