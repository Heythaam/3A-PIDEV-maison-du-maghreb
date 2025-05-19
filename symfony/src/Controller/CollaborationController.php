<?php

namespace App\Controller;
use App\Entity\Collaboration;
use App\Entity\Projet;
use App\Form\CollaborationType;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Doctrine\ORM\EntityManagerInterface;
use Endroid\QrCode\QrCode;
use Endroid\QrCode\Writer\PngWriter;
use Endroid\QrCode\Builder\Builder;
use Endroid\QrCode\Encoding\Encoding;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\String\Slugger\SluggerInterface;
use Knp\Component\Pager\PaginatorInterface;
use Picqer\Barcode\BarcodeGeneratorPNG;
use App\Service\PdfGenerator;
use TCPDF;

final class CollaborationController extends AbstractController{

    private $entityManager;


    public function __construct(EntityManagerInterface $entityManager)
    {
        $this->entityManager = $entityManager;
    }

    #[Route('/collaboration', name: 'app_collaboration')]
    public function index(Request $request, EntityManagerInterface $entityManager, PaginatorInterface $paginator): Response
    {
        $collaborations = $entityManager->getRepository(Collaboration::class)->findAll();
        $paginationCollaboration = $paginator->paginate(
            $collaborations,
            $request->query->getInt('page', 1),
            5
        );

        $projects = $entityManager->getRepository(Projet::class)->findAll();
        $paginationProjet = $paginator->paginate(
            $projects,
            $request->query->getInt('page', 1),
            5
        );
        
        $collaborationProjectRatios = [];
        $collaborationNames = [];
        $ratios = [];
    
        foreach ($collaborations as $collaboration) {
            // Get the associated projects for each collaboration
            $projectCount = count($collaboration->getProjets()); // Assuming getProjets() is the method to get associated projects
    
            // Calculate the ratio (number of projects per collaboration)
            $ratio = $projectCount > 0 ? $projectCount : 0;
    
            // Store data for chart and table
            $collaborationNames[] = $collaboration->getName();
            $ratios[] = $ratio;
    
            // Store ratio for the table
            $collaborationProjectRatios[] = [
                'collaborationName' => $collaboration->getName(),
                'projectCount' => $projectCount,
                'ratio' => $ratio
            ];
        }
    
        
        return $this->render('collaboration/collaboration.html.twig', [
            'paginationCollaboration' => $paginationCollaboration,
            'paginationProjet' => $paginationProjet,
            'collaborationProjectRatios' => $collaborationProjectRatios,
            'collaborationNames' => $collaborationNames, // Send collaboration names for chart
            'ratios' => $ratios, // Send project ratios for chart
        ]);
    }

    #[Route('/ajoutCollaboration', name: 'app_ajoutCollaboration')]
    public function new(Request $request, EntityManagerInterface $entityManager, SluggerInterface $slugger)
    {
        $collaboration = new Collaboration();
        $form = $this->createForm(CollaborationType::class, $collaboration);
        $form->handleRequest($request);
        
        if ($form->isSubmitted() && $form->isValid()) {
            $currentDate = new \DateTime();

            // Set dates from form input
            $collaboration->setStartDate($form->get('startDate')->getData());
            $collaboration->setEndDate($form->get('endDate')->getData());

            // **Status calculation inside submission block**
            if ($collaboration->getStartDate() > $currentDate) {
                $status = "A venir";
            } elseif ($collaboration->getEndDate() < $currentDate) {
                $status = "Terminé";
            } else {
                $status = "Active";
            }
            $collaboration->setStatus($status);

            // Handle image upload
            $imageFile = $form->get('image')->getData();
            if ($imageFile) {
                $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();

                try {
                    $imageFile->move(
                        $this->getParameter('uploads_directory'),
                        $newFilename
                    );
                } catch (FileException $e) {
                    throw new \Exception('Error uploading file: ' . $e->getMessage());
                }

                $collaboration->setImage($newFilename);
            }
            if ($collaboration->getImage() === "") {
                $collaboration->setImage('default.jpg');
            }

            // Generate QR Code
            $qrCode = new QrCode($collaboration->getId()); // Use the collaboration ID or a unique string
            $qrCode->setSize(300);
            $qrCode->setMargin(10);
            $writer = new PngWriter();
            $qrCodePath = 'qr_codes/' . $collaboration->getId() . '.png';
            $writer->writeFile($qrCode, $this->getParameter('uploads_directory') . '/' . $qrCodePath);

            // Save the QR code path to the entity
            $collaboration->setQrCodePath($qrCodePath);

            // Generate Barcode
            $generator = new BarcodeGeneratorPNG();
            $barcodePath = 'barcodes/' . $collaboration->getId() . '.png';
            file_put_contents($this->getParameter('uploads_directory') . '/' . $barcodePath, $generator->getBarcode($collaboration->getId(), BarcodeGeneratorPNG::TYPE_CODE_128));

            // Save the barcode path to the entity
            $collaboration->setBarcodePath($barcodePath);

            // Save to database
            $entityManager->persist($collaboration);
            $entityManager->flush();

            return $this->redirectToRoute('app_listeCollaboration');
        }

        return $this->render('collaboration/ajoutCollaboration.html.twig', [
            'form' => $form->createView(),
        ]);
    }

    #[Route('/listeCollaboration', name: 'app_listeCollaboration')]
    public function listeCollaboration(EntityManagerInterface $entityManager): Response
    {
        return $this->render('collaboration/listeCollaboration.html.twig', [
            'collaborations' => $this->entityManager->getRepository(Collaboration::class)->findAll(),
        ]);
    }
    #[Route('/collaboration/details/{id}', name: 'collaboration_details')]
    public function collaborationDetails(Collaboration $collaboration, EntityManagerInterface $entityManager): Response
    {
        
        $collaboration = $entityManager->getRepository(Collaboration::class)->find($collaboration->getId());
        $projects = $collaboration->getProjets();


        if (!$collaboration) {
            throw $this->createNotFoundException('collaboration not found');
        }

        return $this->render('collaboration/showcollaboration.html.twig', [
            'collaboration' => $collaboration,
            'projets' => $projects,
        ]);
    }
    #[Route('/collaboration/delete/{id}', name: 'delete_collaboration')]
    public function delete(Request $request, Collaboration $collaboration): Response
    {
        if ($this->isCsrfTokenValid('delete'.$collaboration->getId(), $request->request->get('_token'))) {
            $this->entityManager->remove($collaboration);
            $this->entityManager->flush();

            $this->addFlash('success', 'La collaboration a été supprimée avec succès.');
        }

        return $this->redirectToRoute('app_collaboration');
    }
    #[Route("/collaboration/delete/{id}", name:"delete_collaborationback", methods:("POST"))]
    public function deleteBack(Request $request, Collaboration $collaboration): JsonResponse
    {
        // CSRF check and deletion
        if ($this->isCsrfTokenValid('delete' . $collaboration->getId(), $request->request->get('_token'))) {
            $this->entityManager->remove($collaboration);
            $this->entityManager->flush();

            // Return a JSON response indicating success
            return new JsonResponse(['status' => 'success', 'message' => 'Collaboration deleted successfully']);
        }

        // Return an error response if deletion failed
        return new JsonResponse(['status' => 'error', 'message' => 'Failed to delete collaboration'], 400);
    }
    #[Route('/collaboration/edit/{id}', name:'app_editCollaboration')]
    public function edit(Request $request, Collaboration $collaboration, EntityManagerInterface $entityManager, SluggerInterface $slugger): Response
    {
        $form = $this->createForm(CollaborationType::class, $collaboration);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $currentDate = new \DateTime();

            // Set dates from form input
            $collaboration->setStartDate($form->get('startDate')->getData());
            $collaboration->setEndDate($form->get('endDate')->getData());

            // **Status calculation inside submission block**
            if ($collaboration->getStartDate() > $currentDate) {
                $status = "A venir";
            } elseif ($collaboration->getEndDate() < $currentDate) {
                $status = "Terminé";
            } else {
                $status = "Active";
            }
            $collaboration->setStatus($status);

            // Handle image upload
            $imageFile = $form->get('image')->getData();
            if ($imageFile) {
                $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();

                try {
                    $imageFile->move(
                        $this->getParameter('uploads_directory'),
                        $newFilename
                    );
                } catch (FileException $e) {
                    throw new \Exception('Error uploading file: ' . $e->getMessage());
                }

                $collaboration->setImage($newFilename);
            } else {
                $collaboration->setImage('default.jpg'); // **Ensure image is never null**
            }

            $entityManager->persist($collaboration);
            $entityManager->flush();

            return $this->redirectToRoute('app_listeCollaboration');
        }

        return $this->render('collaboration/edit.html.twig', [
            'form' => $form->createView(),
        ]);
    }

    #[Route('/collaboration/editBack/{id}', name: 'app_editCollaborationBack')]
    public function editBack(Request $request, Collaboration $collaboration, EntityManagerInterface $entityManager, SluggerInterface $slugger): Response
    {
        $form = $this->createForm(CollaborationType::class, $collaboration);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $currentDate = new \DateTime();

            // Set dates from form input
            $collaboration->setStartDate($form->get('startDate')->getData());
            $collaboration->setEndDate($form->get('endDate')->getData());

            // **Status calculation inside submission block**
            if ($collaboration->getStartDate() > $currentDate) {
                $status = "A venir";
            } elseif ($collaboration->getEndDate() < $currentDate) {
                $status = "Terminé";
            } else {
                $status = "Active";
            }
            $collaboration->setStatus($status);

            // Handle image upload
            $imageFile = $form->get('image')->getData();
            if ($imageFile) {
                $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();

                try {
                    $imageFile->move(
                        $this->getParameter('uploads_directory'),
                        $newFilename
                    );
                } catch (FileException $e) {
                    throw new \Exception('Error uploading file: ' . $e->getMessage());
                }

                $collaboration->setImage($newFilename);
            }

            // Ensure the image is never null
            if ($collaboration->getImage() === null) {
                $collaboration->setImage('default.jpg');
            }

            // Save to database
            $entityManager->persist($collaboration);
            $entityManager->flush();

            return $this->redirectToRoute('app_collaboration');
        }

        return $this->render('collaboration/ajoutCollaborationBack.html.twig', [
            'form' => $form->createView(),
        ]);
    }
    #[Route('/collaboration/{id}/pdf', name: 'app_collaboration_pdf')]
    public function generatePdf(Collaboration $collaboration, PdfGenerator $pdfGenerator, EntityManagerInterface $entityManager): Response
    {
        $collaboration = $entityManager->getRepository(Collaboration::class)->find($collaboration->getId());

        if (!$collaboration) {
            throw $this->createNotFoundException('Collaboration not found');
        }

        // Generate HTML content for the PDF
        $html = $pdfGenerator->generateHtmlContent($collaboration);

        // Create PDF from HTML (TCPDF or any other library can be used here)
        $pdf = new \TCPDF();
        $pdf->AddPage();
        $pdf->writeHTML($html);
        $pdfContent = $pdf->output('', 'S');  // 'S' means output to string

        // Return PDF as a download response
        $response = new Response($pdfContent);
        $response->headers->set('Content-Type', 'application/pdf');
        $response->headers->set('Content-Disposition', 'attachment; filename="collaboration_' . $collaboration->getId() . '.pdf"');
        $response->headers->set('Content-Transfer-Encoding', 'binary');
        $response->headers->set('Accept-Ranges', 'bytes');

        return $response;
    }
}
